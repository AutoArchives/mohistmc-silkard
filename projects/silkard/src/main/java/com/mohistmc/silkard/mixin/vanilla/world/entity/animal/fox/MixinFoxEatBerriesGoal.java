package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.fox;

import com.mohistmc.silkard.injected.world.entity.ai.goal.ContextMoveToBlockGoal;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(targets = "net.minecraft.world.entity.animal.fox.Fox$FoxEatBerriesGoal")
public class MixinFoxEatBerriesGoal {

    // CraftBukkit start - call EntityChangeBlockEvent
    @Inject(method = "pickSweetBerries", at = @At("HEAD"), cancellable = true)
    private void silkard$changeBlock(BlockState state, CallbackInfo ci) {
        ContextMoveToBlockGoal goal = (ContextMoveToBlockGoal) (Object) this;
        if (!CraftEventFactory.callEntityChangeBlockEvent((Fox) goal.silkard$mob(), goal.silkard$blockPos(), (BlockState) state.setValue(SweetBerryBushBlock.AGE, 1))) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
