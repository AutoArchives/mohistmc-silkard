package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile;

import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(LlamaSpit.class)
public class MixinLlamaSpit {

    // CraftBukkit - projectile hit event
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/LlamaSpit;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    private ProjectileDeflection silkard$preHit(LlamaSpit instance, HitResult hitResult) {
        return instance.preHitTargetOrDeflectSelf(hitResult);
    }

    // CraftBukkit - add Bukkit remove cause
    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/projectile/LlamaSpit;discard()V"))
    private void silkard$despawn(LlamaSpit instance) {
        instance.discard(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/entity/projectile/LlamaSpit;discard()V"))
    private void silkard$despawnInWater(LlamaSpit instance) {
        instance.discard(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Redirect(method = "onHitBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/LlamaSpit;discard()V"))
    private void silkard$hit(LlamaSpit instance) {
        instance.discard(EntityRemoveEvent.Cause.HIT);
    }
}
