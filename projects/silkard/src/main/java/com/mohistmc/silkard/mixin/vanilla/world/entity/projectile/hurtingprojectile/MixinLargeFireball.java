package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile.hurtingprojectile;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.injected.world.entity.projectile.hurtingprojectile.ContextAbstractHurtingProjectile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(LargeFireball.class)
public class MixinLargeFireball {

    @Shadow
    private int explosionPower;

    // CraftBukkit - incendiary follows MOB_GRIEFING
    @Inject(method = "<init>", at = @At("RETURN"))
    private void silkard$incendiary(CallbackInfo ci, @Local(argsOnly = true) Level level) {
        LargeFireball self = (LargeFireball) (Object) this;

        ((ContextAbstractHurtingProjectile) self).silkard$isIncendiary(level instanceof ServerLevel serverLevel && serverLevel.getGameRules().get(GameRules.MOB_GRIEFING));
    }

    // CraftBukkit - set bukkitYield when setting explosionPower
    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void silkard$yield(ValueInput input, CallbackInfo ci) {
        ((ContextAbstractHurtingProjectile) (Object) this).silkard$bukkitYield(this.explosionPower);
    }

    // CraftBukkit - ExplosionPrimeEvent; 'this' is passed as source so the damage cause is known
    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)V"))
    private void silkard$explode(Level level, Entity entity, double x, double y, double z, float power, boolean fire, Level.ExplosionInteraction interaction) {
        ExplosionPrimeEvent event = CraftEventFactory.callExplosionPrimeEvent((org.bukkit.entity.Explosive) entity.getBukkitEntity());

        if (!event.isCancelled()) {
            level.explode(entity, x, y, z, event.getRadius(), event.getFire(), interaction);
        }
    }

    // CraftBukkit - add Bukkit remove cause
    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/hurtingprojectile/LargeFireball;discard()V"))
    private void silkard$discard(LargeFireball instance) {
        instance.discard(EntityRemoveEvent.Cause.HIT);
    }
}
