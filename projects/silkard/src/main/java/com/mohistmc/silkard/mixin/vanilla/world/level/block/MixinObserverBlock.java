package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(ObserverBlock.class)
public abstract class MixinObserverBlock extends DirectionalBlock {

    protected MixinObserverBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // CraftBukkit start
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0), cancellable = true)
    private void silkard$redstoneTurnOff(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (CraftEventFactory.callRedstoneChange(level, pos, 15, 0).getNewCurrent() != 0) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1), cancellable = true)
    private void silkard$redstoneTurnOn(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (CraftEventFactory.callRedstoneChange(level, pos, 0, 15).getNewCurrent() != 15) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
