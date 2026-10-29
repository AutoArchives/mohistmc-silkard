package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile.throwableitemprojectile;

import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(ThrownSplashPotion.class)
public abstract class MixinThrownSplashPotion extends AbstractThrownPotion {

    protected MixinThrownSplashPotion(EntityType<? extends AbstractThrownPotion> type, Level level) {
        super(type, level);
    }

    // CraftBukkit start - potion splash event, configurable intensity and PVP restrictions
    @Overwrite
    public void onHitAsPotion(ServerLevel level, ItemStack itemStack, HitResult hitResult) {
        PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        float durationScale = itemStack.getOrDefault(DataComponents.POTION_DURATION_SCALE, 1.0F);
        Iterable<MobEffectInstance> iterable = potionContents.getAllEffects();
        AABB aabb = this.getBoundingBox().move(hitResult.getLocation().subtract(this.position()));
        AABB aabb1 = aabb.inflate(4.0D, 2.0D, 4.0D);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb1);
        float f1 = ProjectileUtil.computeMargin(this);
        Map<org.bukkit.entity.LivingEntity, Double> affected = new HashMap<>();

        if (!list.isEmpty()) {
            for (LivingEntity livingentity : list) {
                if (!livingentity.isAffectedByPotions()) {
                    continue;
                }

                double d0 = aabb.distanceToSqr(livingentity.getBoundingBox().inflate(f1));
                if (d0 < 16.0D) {
                    double d1 = 1.0D - Math.sqrt(d0) / 4.0D;
                    affected.put((org.bukkit.entity.LivingEntity) livingentity.getBukkitEntity(), d1);
                }
            }
        }

        PotionSplashEvent event = CraftEventFactory.callPotionSplashEvent(this, hitResult, affected);
        if (!event.isCancelled() && !list.isEmpty()) {
            Entity entity1 = this.getEffectSource();
            for (org.bukkit.entity.LivingEntity victim : event.getAffectedEntities()) {
                if (!(victim instanceof CraftLivingEntity)) {
                    continue;
                }

                LivingEntity livingentity = ((CraftLivingEntity) victim).getHandle();
                double d1 = event.getIntensity(victim);

                for (MobEffectInstance mobeffectinstance : iterable) {
                    Holder<MobEffect> holder = mobeffectinstance.getEffect();
                    // CraftBukkit start - Abide by PVP settings - for players only!
                    if (!this.level().getWorld().getPVP() && this.getOwner() instanceof ServerPlayer
                            && livingentity instanceof ServerPlayer && livingentity != this.getOwner()) {
                        MobEffect mobeffect = holder.value();
                        if (mobeffect == MobEffects.SLOWNESS.value() || mobeffect == MobEffects.MINING_FATIGUE.value()
                                || mobeffect == MobEffects.INSTANT_DAMAGE.value() || mobeffect == MobEffects.BLINDNESS.value()
                                || mobeffect == MobEffects.HUNGER.value() || mobeffect == MobEffects.WEAKNESS.value()
                                || mobeffect == MobEffects.POISON.value()) {
                            continue;
                        }
                    }
                    // CraftBukkit end

                    if (holder.value().isInstantaneous()) {
                        holder.value().applyInstantaneousEffect(level, this, this.getOwner(), livingentity, mobeffectinstance.getAmplifier(), d1);
                    } else {
                        int i = mobeffectinstance.mapDuration(duration -> (int) (d1 * duration * durationScale + 0.5));
                        MobEffectInstance mobeffectinstance1 = new MobEffectInstance(holder, i, mobeffectinstance.getAmplifier(), mobeffectinstance.isAmbient(), mobeffectinstance.isVisible());

                        if (!mobeffectinstance1.endsWithin(20)) {
                            ((ContextLivingEntity) livingentity).addEffect(mobeffectinstance1, entity1, EntityPotionEffectEvent.Cause.POTION_SPLASH);
                        }
                    }
                }
            }
        }
    }
    // CraftBukkit end
}
