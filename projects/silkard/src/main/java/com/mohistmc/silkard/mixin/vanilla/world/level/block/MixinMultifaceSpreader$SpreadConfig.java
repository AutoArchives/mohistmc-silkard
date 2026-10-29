package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.block.ContextMultifaceSpreader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(targets = "net.minecraft.world.level.block.MultifaceSpreader$SpreadConfig")
public interface MixinMultifaceSpreader$SpreadConfig {

    @Redirect(method = "placeBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean silkard$spread(LevelAccessor level, BlockPos pos, BlockState state, int flags) {
        BlockPos source = ContextMultifaceSpreader.SOURCE.get();
        return CraftEventFactory.handleBlockSpreadEvent(level, source != null ? source : pos, pos, state, flags);
    }

    @Inject(method = "placeBlock", at = @At("RETURN"))
    private static void silkard$clearSource(LevelAccessor level, MultifaceSpreader.SpreadPos spreadPos, BlockState state, boolean postProcess, CallbackInfoReturnable<Boolean> cir) {
        ContextMultifaceSpreader.SOURCE.remove();
    }
}
