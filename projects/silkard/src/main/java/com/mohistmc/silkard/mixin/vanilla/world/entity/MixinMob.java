package com.mohistmc.silkard.mixin.vanilla.world.entity;

import com.mohistmc.silkard.injected.world.entity.ContextMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
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
    // CraftBukkit end
}
