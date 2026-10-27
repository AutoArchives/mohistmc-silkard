package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.DiodeBlock;
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
@Mixin(ComparatorBlock.class)
public abstract class MixinComparatorBlock extends DiodeBlock {

    protected MixinComparatorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // CraftBukkit start
    @Inject(method = "refreshOutputState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0), cancellable = true)
    private void silkard$redstoneTurnOff(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (CraftEventFactory.callRedstoneChange(level, pos, 15, 0).getNewCurrent() != 0) {
            ci.cancel();
        }
    }

    @Inject(method = "refreshOutputState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1), cancellable = true)
    private void silkard$redstoneTurnOn(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (CraftEventFactory.callRedstoneChange(level, pos, 0, 15).getNewCurrent() != 15) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
