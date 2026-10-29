package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile.hurtingprojectile;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(WitherSkull.class)
public class MixinWitherSkull {

    // CraftBukkit start
    @Redirect(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"))
    private void silkard$heal(LivingEntity instance, float amount, EntityHitResult hitResult) {
        ((ContextLivingEntity) instance).heal(amount, EntityRegainHealthEvent.RegainReason.WITHER);
    }

    @Redirect(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$addEffect(LivingEntity instance, MobEffectInstance effect, Entity source, EntityHitResult hitResult) {
        return ((ContextLivingEntity) instance).addEffect(effect, source, EntityPotionEffectEvent.Cause.ATTACK);
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)V"))
    private void silkard$explode(Level instance, Entity source, double x, double y, double z, float radius, boolean fire, ExplosionInteraction interaction, HitResult hitResult) {
        ExplosionPrimeEvent event = new ExplosionPrimeEvent(((WitherSkull) (Object) this).getBukkitEntity(), radius, fire);
        ((WitherSkull) (Object) this).level().getCraftServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            instance.explode(source, x, y, z, event.getRadius(), event.getFire(), interaction);
        }
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/hurtingprojectile/WitherSkull;discard()V"))
    private void silkard$discard(WitherSkull instance, HitResult hitResult) {
        ((ContextEntity) (Object) instance).discard(EntityRemoveEvent.Cause.HIT);
    }
    // CraftBukkit end
}
