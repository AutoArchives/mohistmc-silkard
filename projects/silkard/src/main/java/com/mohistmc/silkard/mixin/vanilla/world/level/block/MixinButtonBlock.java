package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.ContextLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(ButtonBlock.class)
public class MixinButtonBlock {

    // CraftBukkit start
    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/ButtonBlock;press(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V"), cancellable = true)
    private void silkard$buttonRedstone(BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        boolean powered = state.getValue(ButtonBlock.POWERED);
        org.bukkit.block.Block block = ((ContextLevel) level).getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        int old = (powered) ? 15 : 0;
        int current = (!powered) ? 15 : 0;

        BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, old, current);
        level.getCraftServer().getPluginManager().callEvent(eventRedstone);

        if ((eventRedstone.getNewCurrent() > 0) != (!powered)) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
    // CraftBukkit end
}
