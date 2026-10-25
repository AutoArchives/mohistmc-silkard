package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SculkBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkBlock.class)
public class MixinSculkBlock {

    // CraftBukkit - BlockSpreadEvent guard before the growth block is placed; vanilla playSound only runs when the spread is allowed
    @Inject(method = "attemptUseCharge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"), cancellable = true)
    private void silkard$attemptUseCharge(net.minecraft.world.level.block.SculkSpreader.ChargeCursor cursor, LevelAccessor level, BlockPos originPos, RandomSource random, net.minecraft.world.level.block.SculkSpreader spreader, boolean isBelowBlock, CallbackInfoReturnable<Integer> cir) {
        // TODO 26.3: 锚点已从 attemptReplaceSculk 修正为 attemptUseCharge；BlockSpreadEvent 语义待按 attemptUseCharge 实参重写
    }
}
