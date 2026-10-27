package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BaseTorchBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(RedstoneTorchBlock.class)
public abstract class MixinRedstoneTorchBlock extends BaseTorchBlock {

    protected MixinRedstoneTorchBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // CraftBukkit start
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0), cancellable = true)
    private void silkard$redstoneTurnOff(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        int oldCurrent = state.getValue(RedstoneTorchBlock.LIT) ? 15 : 0;
        BlockRedstoneEvent event = new BlockRedstoneEvent(CraftBlock.at(level, pos), oldCurrent, oldCurrent);
        event.setNewCurrent(0);
        level.getCraftServer().getPluginManager().callEvent(event);
        if (event.getNewCurrent() != 0) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1), cancellable = true)
    private void silkard$redstoneTurnOn(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        int oldCurrent = state.getValue(RedstoneTorchBlock.LIT) ? 15 : 0;
        BlockRedstoneEvent event = new BlockRedstoneEvent(CraftBlock.at(level, pos), oldCurrent, oldCurrent);
        event.setNewCurrent(15);
        level.getCraftServer().getPluginManager().callEvent(event);
        if (event.getNewCurrent() != 15) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
