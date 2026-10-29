package com.mohistmc.silkard.mixin.vanilla.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.context.UseOnContext;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockIgniteEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(FlintAndSteelItem.class)
public class MixinFlintAndSteelItem {

    @Inject(method = "useOn", cancellable = true, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void silkard$ignite(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        silkard$callIgniteEvent(context, cir, context.getClickedPos().relative(context.getClickedFace()));
    }

    @Inject(method = "useOn", cancellable = true, at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void silkard$igniteClicked(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        silkard$callIgniteEvent(context, cir, context.getClickedPos());
    }

    private void silkard$callIgniteEvent(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, BlockPos pos) {
        if (CraftEventFactory.callBlockIgniteEvent(context.getLevel(), pos, BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL, context.getPlayer()).isCancelled()) {
            context.getItemInHand().hurtAndBreak(1, context.getPlayer(), context.getHand().asEquipmentSlot());
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
