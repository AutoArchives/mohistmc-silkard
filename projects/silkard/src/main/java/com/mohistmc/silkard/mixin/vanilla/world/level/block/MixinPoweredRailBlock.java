package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PoweredRailBlock;
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
@Mixin(PoweredRailBlock.class)
public abstract class MixinPoweredRailBlock extends Block {

    protected MixinPoweredRailBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // CraftBukkit start
    @Inject(method = "updateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"), cancellable = true)
    private void silkard$redstoneChange(BlockState state, Level level, BlockPos pos, Block block, CallbackInfo ci) {
        int power = state.getValue(PoweredRailBlock.POWERED) ? 15 : 0;
        int newPower = CraftEventFactory.callRedstoneChange(level, pos, power, 15 - power).getNewCurrent();
        if (newPower == power) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
