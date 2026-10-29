package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.block.ContextMultifaceSpreader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(MultifaceSpreader.class)
public class MixinMultifaceSpreader {

    @Inject(method = "getSpreadFromFaceTowardDirection", at = @At("HEAD"))
    private void silkard$captureSource(BlockState state, BlockGetter level, BlockPos pos, Direction direction, Direction fromFace, MultifaceSpreader.SpreadPredicate predicate, CallbackInfoReturnable<MultifaceSpreader.SpreadPos> cir) {
        ContextMultifaceSpreader.SOURCE.set(pos);
    }
}
