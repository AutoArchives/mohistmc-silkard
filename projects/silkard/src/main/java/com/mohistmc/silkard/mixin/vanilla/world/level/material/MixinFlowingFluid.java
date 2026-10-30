package com.mohistmc.silkard.mixin.vanilla.world.level.material;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/31
 */
@Mixin(FlowingFluid.class)
public abstract class MixinFlowingFluid {

    // @formatter:off
    @Shadow protected abstract FluidState getNewLiquid(ServerLevel level, BlockPos pos, BlockState state);
    @Shadow protected abstract int getSpreadDelay(Level level, BlockPos pos, FluidState oldState, FluidState newState);
    @Shadow protected abstract void spread(ServerLevel level, BlockPos pos, BlockState state, FluidState fluidState);
    @Shadow protected abstract void spreadTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState);
    // @formatter:on

    // CraftBukkit start - FluidLevelChangeEvent
    @Overwrite
    public void tick(ServerLevel level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!fluidState.isSource()) {
            FluidState newLiquid = this.getNewLiquid(level, pos, level.getBlockState(pos));
            int delay = this.getSpreadDelay(level, pos, fluidState, newLiquid);
            if (newLiquid.isEmpty()) {
                fluidState = newLiquid;
                state = Blocks.AIR.defaultBlockState();
                FluidLevelChangeEvent event = CraftEventFactory.callFluidLevelChangeEvent(level, pos, state);
                if (event.isCancelled()) {
                    return;
                }
                state = ((CraftBlockData) event.getNewData()).getState();
                level.setBlockAndUpdate(pos, state);
            } else if (newLiquid != fluidState) {
                fluidState = newLiquid;
                state = fluidState.createLegacyBlock();
                FluidLevelChangeEvent event = CraftEventFactory.callFluidLevelChangeEvent(level, pos, state);
                if (event.isCancelled()) {
                    return;
                }
                state = ((CraftBlockData) event.getNewData()).getState();
                level.setBlockAndUpdate(pos, state);
                level.scheduleTick(pos, fluidState.getType(), delay);
            }
        }
        this.spread(level, pos, state, fluidState);
    }
    // CraftBukkit end

    // CraftBukkit start - BlockFromToEvent
    @Inject(method = "spread", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V"), cancellable = true)
    private void silkard$spreadDown(ServerLevel level, BlockPos pos, BlockState state, FluidState fluidState, CallbackInfo ci) {
        BlockFromToEvent event = new BlockFromToEvent(CraftBlock.at(level, pos), BlockFace.DOWN);
        level.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Redirect(method = "spreadToSides", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V"))
    private static void silkard$spreadToSide(FlowingFluid instance, LevelAccessor level, BlockPos targetPos, BlockState targetState, Direction direction, FluidState targetFluid, @Local(argsOnly = true) ServerLevel serverLevel, @Local(argsOnly = true) BlockPos pos) {
        BlockFromToEvent event = new BlockFromToEvent(CraftBlock.at(serverLevel, pos), CraftBlock.notchToBlockFace(direction));
        serverLevel.getCraftServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            ((MixinFlowingFluid) (Object) instance).spreadTo(level, targetPos, targetState, direction, targetFluid);
        }
    }
    // CraftBukkit end
}
