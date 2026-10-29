package com.mohistmc.silkard.mixin.vanilla.world.level.material;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mohistmc.silkard.injected.world.level.ContextLevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(LavaFluid.class)
public class MixinLavaFluid {

    // Prevent lava putting something on fire
    @WrapWithCondition(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0))
    private static boolean silkard$igniteNeighbour(ServerLevel level, BlockPos target, BlockState state, ServerLevel serverLevel, BlockPos origin, FluidState fluidState, RandomSource random) {
        if (level.getBlockState(target).getBlock() == Blocks.FIRE) {
            return true;
        }
        return !CraftEventFactory.callBlockIgniteEvent(level, target, origin).isCancelled();
    }

    @WrapWithCondition(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1))
    private static boolean silkard$igniteAbove(ServerLevel level, BlockPos target, BlockState state, ServerLevel serverLevel, BlockPos origin, FluidState fluidState, RandomSource random) {
        if (level.getBlockState(target).getBlock() == Blocks.FIRE) {
            return true;
        }
        return !CraftEventFactory.callBlockIgniteEvent(level, target, origin).isCancelled();
    }

    @Inject(method = "spreadTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"), cancellable = true)
    private void silkard$stone(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo ci) {
        if (!CraftEventFactory.handleBlockFormEvent(((ContextLevelAccessor) level).getMinecraftWorld(), pos, Blocks.STONE.defaultBlockState(), 3)) {
            ci.cancel();
        }
    }
}
