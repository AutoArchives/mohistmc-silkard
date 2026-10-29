package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SpongeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(SpongeBlock.class)
public class MixinSpongeBlock {

    @Shadow
    @Final
    private static Direction[] ALL_DIRECTIONS;

    // CraftBukkit start - Use BlockStateListPopulator and fire SpongeAbsorbEvent
    @Overwrite
    private boolean removeWaterBreadthFirstSearch(Level level, BlockPos startPos) {
        BlockStateListPopulator blockList = new BlockStateListPopulator(level);
        BlockPos.breadthFirstTraversal(startPos, 6, 65, (pos, consumer) -> {
            for (Direction direction : ALL_DIRECTIONS) {
                consumer.accept(pos.relative(direction));
            }
        }, pos -> {
            if (pos.equals(startPos)) {
                return BlockPos.TraversalNodeStatus.ACCEPT;
            } else {
                BlockState state = blockList.getBlockState(pos);
                FluidState fluidState = blockList.getFluidState(pos);
                if (!fluidState.is(FluidTags.WATER)) {
                    return BlockPos.TraversalNodeStatus.SKIP;
                } else if (state.getBlock() instanceof BucketPickup bucketPickup && !bucketPickup.pickupBlock((LivingEntity) null, blockList, pos, state).isEmpty()) {
                    return BlockPos.TraversalNodeStatus.ACCEPT;
                } else {
                    if (state.getBlock() instanceof LiquidBlock) {
                        blockList.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    } else {
                        if (!state.is(Blocks.KELP) && !state.is(Blocks.KELP_PLANT) && !state.is(Blocks.SEAGRASS) && !state.is(Blocks.TALL_SEAGRASS)) {
                            return BlockPos.TraversalNodeStatus.SKIP;
                        }

                        blockList.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }

                    return BlockPos.TraversalNodeStatus.ACCEPT;
                }
            }
        });

        List<CraftBlockState> blocks = blockList.getList();
        if (!blocks.isEmpty()) {
            final org.bukkit.block.Block bblock = level.getWorld().getBlockAt(startPos.getX(), startPos.getY(), startPos.getZ());

            SpongeAbsorbEvent event = new SpongeAbsorbEvent(bblock, (List<org.bukkit.block.BlockState>) (List) blocks);
            level.getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return false;
            }

            for (CraftBlockState block : blocks) {
                BlockPos blockpos1 = block.getPosition();
                BlockState blockstate = level.getBlockState(blockpos1);
                FluidState fluidstate = level.getFluidState(blockpos1);

                if (fluidstate.is(FluidTags.WATER)) {
                    if (blockstate.getBlock() instanceof BucketPickup && !((BucketPickup) blockstate.getBlock()).pickupBlock((LivingEntity) null, blockList, blockpos1, blockstate).isEmpty()) {
                        // NOP
                    } else if (blockstate.getBlock() instanceof LiquidBlock) {
                        // NOP
                    } else if (blockstate.is(Blocks.KELP) || blockstate.is(Blocks.KELP_PLANT) || blockstate.is(Blocks.SEAGRASS) || blockstate.is(Blocks.TALL_SEAGRASS)) {
                        BlockEntity blockentity = blockstate.hasBlockEntity() ? level.getBlockEntity(blockpos1) : null;

                        Block.dropResources(blockstate, level, blockpos1, blockentity);
                    }
                }
                level.setBlock(blockpos1, block.getHandle(), block.getFlag());
            }

            return true;
        }
        return false;
    }
    // CraftBukkit end
}
