package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.fox;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.level.block.SweetBerryBushBlock;
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
@Mixin(targets = "net.minecraft.world.entity.animal.fox.Fox$FoxEatBerriesGoal")
public class MixinFoxEatBerriesGoal {

    @Shadow @Final protected PathfinderMob mob;
    @Shadow protected BlockPos blockPos;

    // CraftBukkit start - call EntityChangeBlockEvent
    @Inject(method = "pickSweetBerries", at = @At("HEAD"), cancellable = true)
    private void silkard$changeBlock(BlockState state, CallbackInfo ci) {
        if (!CraftEventFactory.callEntityChangeBlockEvent((Fox) mob, blockPos, (BlockState) state.setValue(SweetBerryBushBlock.AGE, 1))) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
