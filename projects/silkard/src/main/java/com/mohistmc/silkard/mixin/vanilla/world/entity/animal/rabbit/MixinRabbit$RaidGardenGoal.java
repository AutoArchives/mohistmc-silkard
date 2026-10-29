package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.rabbit;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(targets = "net.minecraft.world.entity.animal.rabbit.Rabbit$RaidGardenGoal")
public class MixinRabbit$RaidGardenGoal {

    @Shadow
    @Final
    private Rabbit rabbit;

    // CraftBukkit start - EntityChangeBlockEvent
    @Inject(method = "tick", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private void silkard$eatCarrot(CallbackInfo ci, @Local(ordinal = 0) BlockPos blockPos) {
        if (!CraftEventFactory.callEntityChangeBlockEvent(this.rabbit, blockPos, Blocks.AIR.defaultBlockState())) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private void silkard$ageCarrot(CallbackInfo ci, @Local(ordinal = 0) BlockPos blockPos, @Local(ordinal = 0) BlockState blockState) {
        BlockState newState = blockState.setValue(CarrotBlock.AGE, blockState.getValue(CarrotBlock.AGE) - 1);
        if (!CraftEventFactory.callEntityChangeBlockEvent(this.rabbit, blockPos, newState)) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
