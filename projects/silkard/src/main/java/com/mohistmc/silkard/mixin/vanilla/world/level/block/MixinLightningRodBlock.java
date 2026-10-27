package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.RodBlock;
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
@Mixin(LightningRodBlock.class)
public abstract class MixinLightningRodBlock extends RodBlock {

    protected MixinLightningRodBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // CraftBukkit start
    @Inject(method = "onLightningStrike", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"), cancellable = true)
    private void silkard$redstoneChange(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        boolean powered = state.getValue(LightningRodBlock.POWERED);
        BlockRedstoneEvent event = new BlockRedstoneEvent(CraftBlock.at(level, pos), powered ? 15 : 0, powered ? 0 : 15);
        level.getCraftServer().getPluginManager().callEvent(event);
        if (event.getNewCurrent() <= 0) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
