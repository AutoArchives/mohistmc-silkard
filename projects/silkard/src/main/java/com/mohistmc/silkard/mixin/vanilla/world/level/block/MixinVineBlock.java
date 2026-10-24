package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
// defaultBlockState() 由 Block 声明（public final），@Shadow 命中不了继承成员 → extends Block
@Mixin(VineBlock.class)
public abstract class MixinVineBlock extends net.minecraft.world.level.block.Block {

    protected MixinVineBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties properties) {
        super(properties);
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockSpreadEvent / BlockGrowEvent
     */
    @Overwrite
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getGameRules().get(net.minecraft.world.level.gamerules.GameRules.SPREAD_VINES)) {
            if (random.nextInt(4) == 0) {
                Direction testDirection = Direction.getRandom(random);
                BlockPos abovePos = pos.above();
                if (testDirection.getAxis().isHorizontal() && !state.getValue(VineBlock.getPropertyForFace(testDirection))) {
                    if (this.canSpread(level, pos)) {
                        BlockPos testPos = pos.relative(testDirection);
                        BlockState edgeState = level.getBlockState(testPos);
                        if (edgeState.isAir()) {
                            Direction cwDirection = testDirection.getClockWise();
                            Direction ccwDirection = testDirection.getCounterClockWise();
                            boolean cwHasConnectingFace = state.getValue(VineBlock.getPropertyForFace(cwDirection));
                            boolean ccwHasConnectingFace = state.getValue(VineBlock.getPropertyForFace(ccwDirection));
                            BlockPos cwTestPos = testPos.relative(cwDirection);
                            BlockPos ccwTestPos = testPos.relative(ccwDirection);
                            // CraftBukkit start - Call BlockSpreadEvent
                            BlockPos source = pos;
                            if (cwHasConnectingFace && VineBlock.isAcceptableNeighbour(level, cwTestPos, cwDirection)) {
                                CraftEventFactory.handleBlockSpreadEvent(level, source, testPos, this.defaultBlockState().setValue(VineBlock.getPropertyForFace(cwDirection), true), 2);
                            } else if (ccwHasConnectingFace && VineBlock.isAcceptableNeighbour(level, ccwTestPos, ccwDirection)) {
                                CraftEventFactory.handleBlockSpreadEvent(level, source, testPos, this.defaultBlockState().setValue(VineBlock.getPropertyForFace(ccwDirection), true), 2);
                            } else {
                                Direction opposite = testDirection.getOpposite();
                                if (cwHasConnectingFace && level.isEmptyBlock(cwTestPos) && VineBlock.isAcceptableNeighbour(level, pos.relative(cwDirection), opposite)) {
                                    CraftEventFactory.handleBlockSpreadEvent(level, source, cwTestPos, this.defaultBlockState().setValue(VineBlock.getPropertyForFace(opposite), true), 2);
                                } else if (ccwHasConnectingFace && level.isEmptyBlock(ccwTestPos) && VineBlock.isAcceptableNeighbour(level, pos.relative(ccwDirection), opposite)) {
                                    CraftEventFactory.handleBlockSpreadEvent(level, source, ccwTestPos, this.defaultBlockState().setValue(VineBlock.getPropertyForFace(opposite), true), 2);
                                } else if (random.nextFloat() < 0.05 && VineBlock.isAcceptableNeighbour(level, testPos.above(), Direction.UP)) {
                                    CraftEventFactory.handleBlockSpreadEvent(level, source, testPos, this.defaultBlockState().setValue(VineBlock.UP, true), 2);
                                }
                            }
                            // CraftBukkit end
                        } else if (VineBlock.isAcceptableNeighbour(level, testPos, testDirection)) {
                            // CraftBukkit - BlockGrowEvent
                            CraftEventFactory.handleBlockGrowEvent(level, pos, state.setValue(VineBlock.getPropertyForFace(testDirection), true), 2);
                        }
                    }
                } else {
                    if (testDirection == Direction.UP && pos.getY() < level.getMaxY()) {
                        if (this.canSupportAtFace(level, pos, testDirection)) {
                            // CraftBukkit - BlockGrowEvent
                            CraftEventFactory.handleBlockGrowEvent(level, pos, state.setValue(VineBlock.UP, true), 2);
                            return;
                        }

                        if (level.isEmptyBlock(abovePos)) {
                            if (!this.canSpread(level, pos)) {
                                return;
                            }

                            BlockState aboveState = state;

                            for (Direction direction : Direction.Plane.HORIZONTAL) {
                                if (random.nextBoolean() || !VineBlock.isAcceptableNeighbour(level, abovePos.relative(direction), direction)) {
                                    aboveState = aboveState.setValue(VineBlock.getPropertyForFace(direction), false);
                                }
                            }

                            if (this.hasHorizontalConnection(aboveState)) {
                                // CraftBukkit - BlockSpreadEvent
                                CraftEventFactory.handleBlockSpreadEvent(level, pos, abovePos, aboveState, 2);
                            }

                            return;
                        }
                    }
                }
            }
        }
    }

    // 26.3 的首参是 LevelReader 不是 ServerLevel
    @org.spongepowered.asm.mixin.Shadow
    private boolean canSpread(net.minecraft.world.level.LevelReader level, BlockPos pos) {
        throw new AssertionError();
    }

    @org.spongepowered.asm.mixin.Shadow
    private boolean canSupportAtFace(net.minecraft.world.level.BlockGetter level, BlockPos pos, Direction direction) {
        throw new AssertionError();
    }

    @org.spongepowered.asm.mixin.Shadow
    private boolean hasHorizontalConnection(BlockState state) {
        throw new AssertionError();
    }

    @org.spongepowered.asm.mixin.Shadow
    public static boolean isAcceptableNeighbour(net.minecraft.world.level.BlockGetter level, BlockPos pos, Direction direction) {
        throw new AssertionError();
    }

    @org.spongepowered.asm.mixin.Shadow
    public static net.minecraft.world.level.block.state.properties.BooleanProperty getPropertyForFace(Direction direction) {
        throw new AssertionError();
    }
}
