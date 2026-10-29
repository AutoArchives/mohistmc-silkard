package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.SculkVeinBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(SculkVeinBlock.class)
public abstract class MixinSculkVeinBlock {

    @Shadow
    @Final
    private MultifaceSpreader veinSpreader;

    @Shadow
    public abstract void onDischarged(LevelAccessor level, BlockState state, BlockPos pos, RandomSource random);

    @Unique
    private BlockPos silkard$source;

    @Inject(method = "attemptUseCharge", at = @At("HEAD"))
    private void silkard$captureSource(SculkSpreader.ChargeCursor cursor, LevelAccessor level, BlockPos originPos, RandomSource random, SculkSpreader spreader, boolean spreadVeins, CallbackInfoReturnable<Integer> cir) {
        this.silkard$source = originPos;
    }

    @Inject(method = "attemptPlaceSculk", at = @At("HEAD"), cancellable = true)
    private void silkard$attemptPlace(SculkSpreader spreader, LevelAccessor level, BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.silkard$placeSculk(spreader, level, pos, random, this.silkard$source != null ? this.silkard$source : pos));
    }

    // CraftBukkit start - Call BlockSpreadEvent
    @Unique
    private boolean silkard$placeSculk(SculkSpreader spreader, LevelAccessor level, BlockPos pos, RandomSource random, BlockPos sourceBlock) {
        BlockState blockstate = level.getBlockState(pos);
        TagKey<Block> tagkey = spreader.replaceableBlocks();

        for (Direction direction : Direction.allShuffled(random)) {
            if (!MultifaceBlock.hasFace(blockstate, direction)) {
                continue;
            }

            BlockPos blockpos1 = pos.relative(direction);
            BlockState blockstate1 = level.getBlockState(blockpos1);
            if (!blockstate1.is(tagkey)) {
                continue;
            }

            BlockState blockstate2 = Blocks.SCULK.defaultBlockState();
            if (!CraftEventFactory.handleBlockSpreadEvent(level, sourceBlock, blockpos1, blockstate2, 3)) {
                return false;
            }

            Block.pushEntitiesUp(blockstate1, blockstate2, level, blockpos1);
            level.playSound((Entity) null, blockpos1, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.veinSpreader.spreadAll(blockstate2, level, blockpos1, spreader.isWorldGeneration());

            for (Direction direction1 : Direction.values()) {
                if (direction1 == direction.getOpposite()) {
                    continue;
                }

                BlockPos blockpos2 = blockpos1.relative(direction1);
                BlockState blockstate3 = level.getBlockState(blockpos2);
                if (blockstate3.is(Blocks.SCULK_VEIN)) {
                    this.onDischarged(level, blockstate3, blockpos2, random);
                }
            }

            return true;
        }

        return false;
    }
    // CraftBukkit end
}
