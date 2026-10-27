package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(CampfireBlock.class)
public class MixinCampfireBlock {

    // CraftBukkit - let plugins see the campfire as the damage source
    @Redirect(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;campfire()Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource silkard$campfireDamage(DamageSources sources, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        return sources.campfire().directBlock(level, pos);
    }

    @Inject(method = "onProjectileHit", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void silkard$ignite(Level level, BlockState state, BlockHitResult hitResult, Projectile projectile, CallbackInfo ci) {
        if (CraftEventFactory.callBlockIgniteEvent(level, hitResult.getBlockPos(), projectile).isCancelled()) {
            ci.cancel();
        }
    }
}
