package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.EyeblossomBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EyeblossomBlock.class)
public class MixinEyeblossomBlock {

    // 26.3 moved setBlockAndUpdate from tick into tryChangingState
    @Inject(method = "tryChangingState", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"), cancellable = true)
    private void silkard$tryChangingState(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        if (!CraftEventFactory.handleBlockFormEvent(level, pos, ((EyeblossomBlock) (Object) this).type.transform().state(), 3)) {
            cir.setReturnValue(false);
        }
    }
}
