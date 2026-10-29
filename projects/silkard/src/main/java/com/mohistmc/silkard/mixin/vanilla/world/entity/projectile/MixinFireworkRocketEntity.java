package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import com.mohistmc.silkard.injected.world.entity.projectile.ContextProjectile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(FireworkRocketEntity.class)
public abstract class MixinFireworkRocketEntity {

    @Shadow
    private void explode(ServerLevel level) {
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    private ProjectileDeflection silkard$hitTargetOrDeflectSelf(FireworkRocketEntity self, HitResult hitResult) {
        return ((ContextProjectile) (Object) self).preHitTargetOrDeflectSelf(hitResult);
    }

    @Redirect(method = {"tick", "onHitEntity", "onHitBlock"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;explode(Lnet/minecraft/server/level/ServerLevel;)V"))
    private void silkard$explode(FireworkRocketEntity self, ServerLevel level) {
        if (!CraftEventFactory.callFireworkExplodeEvent(self).isCancelled()) {
            ((MixinFireworkRocketEntity) (Object) self).explode(level);
        }
    }

    @Redirect(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;discard()V"))
    private void silkard$discard(FireworkRocketEntity self) {
        ((ContextEntity) (Object) self).discard(EntityRemoveEvent.Cause.EXPLODE);
    }
}
