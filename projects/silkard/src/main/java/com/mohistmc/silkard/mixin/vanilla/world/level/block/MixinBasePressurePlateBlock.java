package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(BasePressurePlateBlock.class)
public abstract class MixinBasePressurePlateBlock extends BasePressurePlateBlock {

    @Unique private int silkard$oldSignal;

    protected MixinBasePressurePlateBlock(BlockBehaviour.Properties properties, BlockSetType type) {
        super(properties, type);
    }

    // CraftBukkit start - BlockRedstoneEvent on pressure plate signal change
    @Inject(method = "checkPressed", at = @At("HEAD"))
    private void silkard$captureOldSignal(Entity entity, Level level, BlockPos pos, BlockState state, int oldSignal, CallbackInfo ci) {
        this.silkard$oldSignal = oldSignal;
    }

    @Redirect(method = "checkPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BasePressurePlateBlock;getSignalStrength(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)I"))
    private int silkard$redstoneChange(Level level, BlockPos pos) {
        int signal = this.getSignalStrength(level, pos);
        int oldSignal = this.silkard$oldSignal;
        if ((oldSignal > 0) != (signal > 0)) {
            signal = CraftEventFactory.callRedstoneChange(level, pos, oldSignal, signal).getNewCurrent();
        }
        return signal;
    }
    // CraftBukkit end
}
