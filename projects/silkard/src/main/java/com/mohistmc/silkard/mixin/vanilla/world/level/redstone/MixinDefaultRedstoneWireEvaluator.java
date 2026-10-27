package com.mohistmc.silkard.mixin.vanilla.world.level.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.DefaultRedstoneWireEvaluator;
import net.minecraft.world.level.redstone.Orientation;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(DefaultRedstoneWireEvaluator.class)
public abstract class MixinDefaultRedstoneWireEvaluator {

    @Shadow
    private int calculateTargetStrength(Level level, BlockPos pos) {
        throw new AssertionError();
    }

    @Unique private BlockState silkard$state;

    // CraftBukkit start
    @Inject(method = "updatePowerStrength", at = @At("HEAD"))
    private void silkard$captureState(Level level, BlockPos pos, BlockState state, Orientation orientation, boolean skipShapeUpdates, CallbackInfo ci) {
        this.silkard$state = state;
    }

    @Redirect(method = "updatePowerStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/redstone/DefaultRedstoneWireEvaluator;calculateTargetStrength(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)I"))
    private int silkard$redstoneChange(Level level, BlockPos pos) {
        int strength = this.calculateTargetStrength(level, pos);
        int oldPower = this.silkard$state.getValue(RedstoneWireBlock.POWER);
        if (oldPower != strength) {
            strength = CraftEventFactory.callRedstoneChange(level, pos, oldPower, strength).getNewCurrent();
        }
        return strength;
    }
    // CraftBukkit end
}
