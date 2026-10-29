package com.mohistmc.silkard.mixin.vanilla.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.context.UseOnContext;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockIgniteEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(FireChargeItem.class)
public class MixinFireChargeItem {

    private static final String PLAY_SOUND = "Lnet/minecraft/world/item/FireChargeItem;playSound(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V";

    @Inject(method = "useOn", cancellable = true, at = @At(value = "INVOKE", ordinal = 0, target = PLAY_SOUND))
    private void silkard$ignite(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        silkard$callIgniteEvent(context, cir, context.getClickedPos().relative(context.getClickedFace()));
    }

    @Inject(method = "useOn", cancellable = true, at = @At(value = "INVOKE", ordinal = 1, target = PLAY_SOUND))
    private void silkard$igniteClicked(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        silkard$callIgniteEvent(context, cir, context.getClickedPos());
    }

    private void silkard$callIgniteEvent(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, BlockPos pos) {
        if (CraftEventFactory.callBlockIgniteEvent(context.getLevel(), pos, BlockIgniteEvent.IgniteCause.FIREBALL, context.getPlayer()).isCancelled()) {
            if (!context.getPlayer().getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
