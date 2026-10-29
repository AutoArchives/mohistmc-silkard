package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile.throwableitemprojectile;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownExperienceBottle;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(ThrownExperienceBottle.class)
public class MixinThrownExperienceBottle {

    @Unique
    private HitResult silkard$hitResult;

    @Inject(method = "onHit", at = @At("HEAD"))
    private void silkard$captureHit(HitResult hitResult, CallbackInfo ci) {
        this.silkard$hitResult = hitResult;
    }

    @WrapWithCondition(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;levelEvent(ILnet/minecraft/core/BlockPos;I)V", ordinal = 0))
    private boolean silkard$deferEffect(ServerLevel level, int type, BlockPos pos, int data) {
        return false;
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;awardWithDirection(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;I)V"))
    private void silkard$award(ServerLevel level, Vec3 location, Vec3 direction, int exp) {
        ExpBottleEvent event = CraftEventFactory.callExpBottleEvent((Entity) (Object) this, this.silkard$hitResult, exp);
        if (event.getShowEffect()) {
            level.levelEvent(2002, ((Entity) (Object) this).blockPosition(), -13083194);
        }
        ExperienceOrb.awardWithDirection(level, location, direction, event.getExperience());
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/ThrownExperienceBottle;discard()V"))
    private void silkard$discard(ThrownExperienceBottle entity) {
        ((ContextEntity) (Object) entity).discard(EntityRemoveEvent.Cause.HIT);
    }
}
