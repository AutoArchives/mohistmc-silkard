package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(FlowerPotBlock.class)
public class MixinFlowerPotBlock {

    @Unique
    private boolean silkard$formed;

    // CraftBukkit - BlockFormEvent
    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$setBlock(ServerLevel level, BlockPos pos, BlockState state) {
        return this.silkard$formed = CraftEventFactory.handleBlockFormEvent(level, pos, state, 3);
    }

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/EyeblossomBlock$Type;spawnTransformParticle(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V"), cancellable = true)
    private void silkard$blockFormed(CallbackInfo ci) {
        if (!this.silkard$formed) {
            ci.cancel();
        }
    }
}
