package com.mohistmc.silkard.mixin.vanilla.world.entity;

import com.mohistmc.silkard.injected.world.entity.ContextConversionType;
import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import com.mohistmc.silkard.injected.world.entity.ContextMob;
import com.mohistmc.silkard.injected.world.level.ContextLevelWriter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/4/2 20:01
 */
@Mixin(Mob.class)
public abstract class MixinMob implements ContextMob {

    @Unique
    public boolean aware = true; // CraftBukkit

    @Shadow
    @Nullable
    private LivingEntity target;

    @Shadow
    public abstract @Nullable LivingEntity getTarget();

    @Shadow
    protected abstract @Nullable LivingEntity asValidTarget(@Nullable LivingEntity target);

    @Override
    public boolean silkard_aware() {
        return aware;
    }

    @Override
    public void silkard_aware(boolean aware){
        this.aware = aware;
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - delegate to event overload
     */
    @Overwrite
    public void setTarget(@Nullable LivingEntity target) {
        // CraftBukkit start - fire event
        this.setTarget(target, EntityTargetEvent.TargetReason.UNKNOWN, true);
    }

    // CraftBukkit start
    @Override
    public boolean setTarget(@Nullable LivingEntity target, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        return ((ContextMob) (Object) this).silkard$superSetTarget(target, reason, fireEvent);
    }

    @Override
    public boolean silkard$superSetTarget(@Nullable LivingEntity target, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        Mob handle = (Mob) (Object) this;
        target = this.asValidTarget(target);
        if (getTarget() == target) return false;
        if (fireEvent) {
            if (reason == EntityTargetEvent.TargetReason.UNKNOWN && getTarget() != null && target == null) {
                reason = getTarget().isAlive() ? EntityTargetEvent.TargetReason.FORGOT_TARGET : EntityTargetEvent.TargetReason.TARGET_DIED;
            }
            if (reason == EntityTargetEvent.TargetReason.UNKNOWN) {
                handle.level().getCraftServer().getLogger().log(java.util.logging.Level.WARNING, "Unknown target reason, please report on the issue tracker", new Exception());
            }
            CraftLivingEntity ctarget = null;
            if (target != null) {
                ctarget = (CraftLivingEntity) target.getBukkitEntity();
            }
            EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(handle.getBukkitEntity(), ctarget, reason);
            handle.level().getCraftServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            if (event.getTarget() != null) {
                target = ((CraftLivingEntity) event.getTarget()).getHandle();
            } else {
                target = null;
            }
        }
        this.target = target;
        return true;
    }

    // CraftBukkit start - conversion events (4-arg convertTo kept vanilla for Fabric @Inject anchors)
    @Override
    public <T extends Mob> @Nullable T convertTo(EntityType<T> entityType, ConversionParams params, ConversionParams.AfterConversion<T> afterConversion, EntityTransformEvent.TransformReason transformReason, CreatureSpawnEvent.SpawnReason spawnReason) {
        return ((ContextMob) (Object) this).convertTo(entityType, params, EntitySpawnReason.CONVERSION, afterConversion, transformReason, spawnReason);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Mob> @Nullable T convertTo(EntityType<T> entityType, ConversionParams params, EntitySpawnReason spawnReason, ConversionParams.AfterConversion<T> afterConversion, EntityTransformEvent.TransformReason transformReason, CreatureSpawnEvent.SpawnReason bukkitSpawnReason) {
        if (((Mob) (Object) this).isRemoved()) {
            return null;
        }
        T converted = (T) entityType.create(((Mob) (Object) this).level(), spawnReason);
        if (converted == null) {
            return null;
        }
        params.type().convert((Mob) (Object) this, converted, params);
        afterConversion.finalizeConversion(converted);
        if (transformReason == null) {
            return converted;
        }
        if (CraftEventFactory.callEntityTransformEvent((LivingEntity) (Object) this, converted, transformReason).isCancelled()) {
            return null;
        }
        ((ContextConversionType) params.type()).postConvert((Mob) (Object) this, converted, params);
        if (((Mob) (Object) this).level() instanceof ServerLevel serverLevel) {
            ((ContextLevelWriter) serverLevel).addFreshEntity(converted, bukkitSpawnReason);
        }
        if (params.type().shouldDiscardAfterConversion()) {
            ((ContextEntity) (Object) this).discard(EntityRemoveEvent.Cause.TRANSFORMATION);
        }
        return converted;
    }
    // CraftBukkit end
}
