package com.mohistmc.silkard.mixin.vanilla.world.level.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.ExperimentalRedstoneWireEvaluator;
import net.minecraft.world.level.redstone.Orientation;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(ExperimentalRedstoneWireEvaluator.class)
public abstract class MixinExperimentalRedstoneWireEvaluator {

    @Unique private Level silkard$level;

    // CraftBukkit start
    @Inject(method = "updatePowerStrength", at = @At("HEAD"))
    private void silkard$captureLevel(Level level, BlockPos pos, BlockState state, Orientation orientation, boolean skipShapeUpdates, CallbackInfo ci) {
        this.silkard$level = level;
    }

    @ModifyArgs(method = "updatePowerStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void silkard$redstoneChange(Args args) {
        BlockPos pos = args.get(0);
        BlockState state = args.get(1);
        int newPower = state.getValue(RedstoneWireBlock.POWER);
        int oldPower = this.silkard$level.getBlockState(pos).getValue(RedstoneWireBlock.POWER);
        if (oldPower != newPower) {
            int current = CraftEventFactory.callRedstoneChange(this.silkard$level, pos, oldPower, newPower).getNewCurrent();
            if (current != newPower) {
                args.set(1, (BlockState) state.setValue(RedstoneWireBlock.POWER, current));
            }
        }
    }
    // CraftBukkit end
}
