package com.mohistmc.silkard.mixin.vanilla.world.entity.monster;

import com.mohistmc.silkard.injected.world.entity.projectile.hurtingprojectile.ContextAbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.entity.monster.Ghast$GhastShootFireballGoal")
public class MixinGhast$GhastShootFireballGoal {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/hurtingprojectile/LargeFireball;setPos(DDD)V"))
    private void silkard$setPos(LargeFireball instance, double x, double y, double z) {
        // CraftBukkit - set bukkitYield when setting explosionpower
        ((ContextAbstractHurtingProjectile) instance).silkard$bukkitYield(instance.explosionPower);
        instance.setPos(x, y, z);
    }
}
