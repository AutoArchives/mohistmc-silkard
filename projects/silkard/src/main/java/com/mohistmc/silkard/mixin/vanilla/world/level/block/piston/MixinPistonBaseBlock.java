package com.mohistmc.silkard.mixin.vanilla.world.level.block.piston;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(PistonBaseBlock.class)
public class MixinPistonBaseBlock {

    @Shadow
    @Final
    private boolean isSticky;

    // CraftBukkit start - BlockPistonRetractEvent for non-sticky pistons
    @Inject(method = "checkIfExtend", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;blockEvent(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;II)V", ordinal = 1), cancellable = true)
    private void silkard$retractEvent(Level level, BlockPos pos, BlockState state, CallbackInfo ci, @Local(index = 4) Direction direction) {
        if (!this.isSticky) {
            BlockPistonRetractEvent event = new BlockPistonRetractEvent(CraftBlock.at(level, pos), Collections.emptyList(), CraftBlock.notchToBlockFace(direction));
            level.getCraftServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }
    // CraftBukkit end

    // CraftBukkit start - BlockPistonExtendEvent / BlockPistonRetractEvent with the moved blocks
    @Inject(method = "moveBlocks", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0), cancellable = true)
    private void silkard$pistonEvent(Level level, BlockPos pos, Direction direction, boolean extending, CallbackInfoReturnable<Boolean> cir,
                                     @Local(index = 8) List<BlockPos> moved, @Local(index = 10) List<BlockPos> broken) {
        Direction direction1 = extending ? direction : direction.getOpposite();
        org.bukkit.block.Block pistonBlock = level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        List<org.bukkit.block.Block> blocks = new AbstractList<>() {

            @Override
            public int size() {
                return moved.size() + broken.size();
            }

            @Override
            public org.bukkit.block.Block get(int index) {
                if (index >= size() || index < 0) {
                    throw new ArrayIndexOutOfBoundsException(index);
                }
                BlockPos blockPos = index < moved.size() ? moved.get(index) : broken.get(index - moved.size());
                return pistonBlock.getWorld().getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            }
        };
        BlockPistonEvent event = extending
                ? new BlockPistonExtendEvent(pistonBlock, blocks, CraftBlock.notchToBlockFace(direction1))
                : new BlockPistonRetractEvent(pistonBlock, blocks, CraftBlock.notchToBlockFace(direction1));
        level.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            for (BlockPos blockPos : broken) {
                level.sendBlockUpdated(blockPos, Blocks.AIR.defaultBlockState(), level.getBlockState(blockPos), 3);
            }
            for (BlockPos blockPos : moved) {
                level.sendBlockUpdated(blockPos, Blocks.AIR.defaultBlockState(), level.getBlockState(blockPos), 3);
                blockPos = blockPos.relative(direction1);
                level.sendBlockUpdated(blockPos, Blocks.AIR.defaultBlockState(), level.getBlockState(blockPos), 3);
            }
            cir.setReturnValue(false);
        }
    }
    // CraftBukkit end
}
