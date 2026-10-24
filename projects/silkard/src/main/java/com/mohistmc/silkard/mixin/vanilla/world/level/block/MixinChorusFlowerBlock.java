package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(ChorusFlowerBlock.class)
public class MixinChorusFlowerBlock {

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockSpreadEvent / BlockGrowEvent
     */
    @Overwrite
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos above = pos.above();
        if (level.isEmptyBlock(above) && above.getY() <= level.getMaxY()) {
            int currentAge = state.getValue(ChorusFlowerBlock.AGE);
            if (currentAge < 5) {
                boolean growUpwards = false;
                boolean pillarOnSupportBlock = false;
                BlockState belowState = level.getBlockState(pos.below());
                if (belowState.is(net.minecraft.tags.BlockTags.SUPPORTS_CHORUS_FLOWER)) {
                    growUpwards = true;
                } else if (belowState.is(this.plant)) {
                    int height = 1;

                    for (int i = 0; i < 4; i++) {
                        BlockState testState = level.getBlockState(pos.below(height + 1));
                        if (!testState.is(this.plant)) {
                            if (testState.is(net.minecraft.tags.BlockTags.SUPPORTS_CHORUS_FLOWER)) {
                                pillarOnSupportBlock = true;
                            }
                            break;
                        }

                        height++;
                    }

                    if (height < 2 || height <= random.nextInt(pillarOnSupportBlock ? 5 : 4)) {
                        growUpwards = true;
                    }
                } else if (belowState.isAir()) {
                    growUpwards = true;
                }

                if (growUpwards && allNeighborsEmpty(level, above, null) && level.isEmptyBlock(pos.above(2))) {
                    // CraftBukkit start - add event
                    if (CraftEventFactory.handleBlockSpreadEvent(level, pos, above, this.defaultBlockState().setValue(ChorusFlowerBlock.AGE, currentAge), 2)) {
                        level.setBlock(pos, ChorusPlantBlock.getStateWithConnections(level, pos, this.plant.defaultBlockState()), 2);
                        this.placeGrownFlower(level, above, currentAge);
                    }
                    // CraftBukkit end
                } else if (currentAge < 4) {
                    int numBranchAttempts = random.nextInt(4);
                    if (pillarOnSupportBlock) {
                        numBranchAttempts++;
                    }

                    boolean createdBranch = false;

                    for (int i = 0; i < numBranchAttempts; i++) {
                        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                        BlockPos target = pos.relative(direction);
                        if (level.isEmptyBlock(target) && level.isEmptyBlock(target.below()) && allNeighborsEmpty(level, target, direction.getOpposite())) {
                            // CraftBukkit start - add event
                            if (CraftEventFactory.handleBlockSpreadEvent(level, pos, target, this.defaultBlockState().setValue(ChorusFlowerBlock.AGE, currentAge + 1), 2)) {
                                this.placeGrownFlower(level, target, currentAge + 1);
                                createdBranch = true;
                            }
                            // CraftBukkit end
                        }
                    }

                    if (createdBranch) {
                        level.setBlock(pos, ChorusPlantBlock.getStateWithConnections(level, pos, this.plant.defaultBlockState()), 2);
                    } else {
                        // CraftBukkit start - add event
                        if (CraftEventFactory.handleBlockGrowEvent(level, pos, this.defaultBlockState().setValue(ChorusFlowerBlock.AGE, 5), 2)) {
                            this.placeDeadFlower(level, pos);
                        }
                        // CraftBukkit end
                    }
                } else {
                    // CraftBukkit start - add event
                    if (CraftEventFactory.handleBlockGrowEvent(level, pos, this.defaultBlockState().setValue(ChorusFlowerBlock.AGE, 5), 2)) {
                        this.placeDeadFlower(level, pos);
                    }
                    // CraftBukkit end
                }
            }
        }
    }

    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    private net.minecraft.world.level.block.Block plant;

    @org.spongepowered.asm.mixin.Shadow
    protected BlockState defaultBlockState() {
        throw new AssertionError();
    }

    @org.spongepowered.asm.mixin.Shadow
    private void placeGrownFlower(Level level, BlockPos pos, int age) {
        throw new AssertionError();
    }

    @org.spongepowered.asm.mixin.Shadow
    private void placeDeadFlower(Level level, BlockPos pos) {
        throw new AssertionError();
    }

    @org.spongepowered.asm.mixin.Shadow
    private static boolean allNeighborsEmpty(net.minecraft.world.level.LevelReader level, BlockPos pos, @org.jspecify.annotations.Nullable Direction ignore) {
        throw new AssertionError();
    }
}
