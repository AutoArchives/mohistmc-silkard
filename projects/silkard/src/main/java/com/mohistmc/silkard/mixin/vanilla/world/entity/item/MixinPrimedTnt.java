package com.mohistmc.silkard.mixin.vanilla.world.entity.item;

import com.mohistmc.silkard.injected.world.entity.item.ContextPrimedTnt;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Explosive;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/5/1 18:31
 */
@Mixin(PrimedTnt.class)
public class MixinPrimedTnt implements ContextPrimedTnt {

    public boolean isIncendiary = false; // CraftBukkit - add field

    public boolean silkard$isIncendiary() {
        return isIncendiary;
    }

    public void silkard$isIncendiary(boolean isIncendiary) {
        this.isIncendiary = isIncendiary;
    }

    @Unique
    private float silkard$radius;
    @Unique
    private boolean silkard$fire;

    // CraftBukkit start - discard after the explosion so the prime event still has a location
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/PrimedTnt;discard()V"))
    private void silkard$deferDiscard(PrimedTnt instance) {
        if (instance.level().isClientSide()) {
            instance.discard();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/PrimedTnt;explode()V", shift = At.Shift.AFTER))
    private void silkard$discardAfterExplode(CallbackInfo ci) {
        PrimedTnt self = (PrimedTnt) (Object) this;
        if (!self.level().isClientSide()) {
            self.discard(EntityRemoveEvent.Cause.EXPLODE);
        }
    }

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void silkard$explosionPrime(CallbackInfo ci) {
        PrimedTnt self = (PrimedTnt) (Object) this;
        this.silkard$radius = self.explosionPower;
        this.silkard$fire = false;
        if (self.level() instanceof ServerLevel serverlevel && serverlevel.getGameRules().get(GameRules.TNT_EXPLODES)) {
            ExplosionPrimeEvent event = CraftEventFactory.callExplosionPrimeEvent((Explosive) self.getBukkitEntity());
            if (event.isCancelled()) {
                ci.cancel();
                return;
            }
            this.silkard$radius = event.getRadius();
            this.silkard$fire = event.getFire();
        }
    }

    @Redirect(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)V"))
    private void silkard$explode(Level level, Entity source, DamageSource damageSource, ExplosionDamageCalculator calculator, double x, double y, double z, float power, boolean fire, Level.ExplosionInteraction interaction) {
        level.explode(source, damageSource, calculator, x, y, z, this.silkard$radius, this.silkard$fire, interaction); // CraftBukkit - from the prime event
    }
    // CraftBukkit end
}
