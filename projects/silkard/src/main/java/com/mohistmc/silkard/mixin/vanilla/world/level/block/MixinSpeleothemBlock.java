package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/31
 */
@Mixin(SpeleothemBlock.class)
public class MixinSpeleothemBlock {

    // CraftBukkit start - EntityChangeBlockEvent
    @Inject(method = "onProjectileHit", at = @At("HEAD"), cancellable = true)
    private void silkard$projectileBreak(Level level, BlockState state, BlockHitResult hitResult, Projectile projectile, CallbackInfo ci) {
        if (level instanceof ServerLevel serverLevel
                && projectile.mayInteract(serverLevel, hitResult.getBlockPos())
                && projectile.mayBreak(serverLevel, hitResult.getBlockPos())
                && projectile instanceof ThrownTrident
                && projectile.getDeltaMovement().length() > 0.6D
                && !CraftEventFactory.callEntityChangeBlockEvent(projectile, hitResult.getBlockPos(), Blocks.AIR.defaultBlockState())) {
            ci.cancel();
        }
    }
    // CraftBukkit end

    // CraftBukkit start - BlockSpreadEvent
    @Redirect(method = "grow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SpeleothemBlock;createSpeleothem(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/properties/SpeleothemThickness;)V"))
    private static void silkard$growCreateSpeleothem(SpeleothemBlock instance, LevelAccessor level, BlockPos pos, Direction direction, SpeleothemThickness thickness, @Local(argsOnly = true) BlockPos source) {
        silkard$createSpeleothem(instance, level, pos, direction, thickness, source);
    }

    @Redirect(method = "createMergedTips", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SpeleothemBlock;createSpeleothem(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/properties/SpeleothemThickness;)V"))
    private static void silkard$mergedCreateSpeleothem(SpeleothemBlock instance, LevelAccessor level, BlockPos pos, Direction direction, SpeleothemThickness thickness, @Local(argsOnly = true) BlockPos source) {
        silkard$createSpeleothem(instance, level, pos, direction, thickness, source);
    }

    @Unique
    private static void silkard$createSpeleothem(SpeleothemBlock instance, LevelAccessor level, BlockPos pos, Direction direction, SpeleothemThickness thickness, BlockPos source) {
        BlockState state = ((Block) (Object) instance).defaultBlockState()
                .setValue(SpeleothemBlock.TIP_DIRECTION, direction)
                .setValue(SpeleothemBlock.THICKNESS, thickness)
                .setValue(SpeleothemBlock.WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER));
        CraftEventFactory.handleBlockSpreadEvent(level, source, pos, state, 3);
    }
    // CraftBukkit end
}
