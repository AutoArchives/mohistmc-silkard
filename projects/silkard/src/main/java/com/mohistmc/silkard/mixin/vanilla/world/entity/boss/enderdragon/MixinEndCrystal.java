package com.mohistmc.silkard.mixin.vanilla.world.entity.boss.enderdragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(EndCrystal.class)
public class MixinEndCrystal {

    // CraftBukkit - BlockIgniteEvent when the crystal sets fire
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$ignite(ServerLevel level, BlockPos pos, BlockState state) {
        if (CraftEventFactory.callBlockIgniteEvent(level, pos, (Entity) (Object) this).isCancelled()) {
            return false;
        }
        return level.setBlockAndUpdate(pos, state);
    }

    // CraftBukkit - all non-living entities need a damage event
    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void silkard$damage(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (CraftEventFactory.handleNonLivingEntityDamageEvent((Entity) (Object) this, source, damage, false)) {
            cir.setReturnValue(false);
        }
    }

    // CraftBukkit - add Bukkit remove cause
    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void silkard$remove(Entity instance, Entity.RemovalReason reason, @Local(argsOnly = true) DamageSource source) {
        instance.remove(reason, source.is(DamageTypeTags.IS_EXPLOSION) ? EntityRemoveEvent.Cause.DEATH : EntityRemoveEvent.Cause.EXPLODE);
    }

    // CraftBukkit - ExplosionPrimeEvent
    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)V"))
    private void silkard$explode(ServerLevel level, Entity entity, DamageSource damageSource, ExplosionDamageCalculator calculator,
                                 double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction interaction) {
        ExplosionPrimeEvent event = CraftEventFactory.callExplosionPrimeEvent(entity, radius, fire);

        if (event.isCancelled()) {
            return;
        }
        level.explode(entity, damageSource, calculator, x, y, z, event.getRadius(), event.getFire(), interaction);
    }
}
