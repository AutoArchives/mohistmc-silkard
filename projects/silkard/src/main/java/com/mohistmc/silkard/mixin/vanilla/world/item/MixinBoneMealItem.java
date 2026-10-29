package com.mohistmc.silkard.mixin.vanilla.world.item;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(BoneMealItem.class)
public class MixinBoneMealItem {

    // CraftBukkit - SPIGOT-7518: dispensers have no player
    @Redirect(method = "useOn", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/item/ItemStack;causeUseVibration(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder$Reference;)V"))
    private void silkard$vibration(ItemStack stack, Entity player, Holder.Reference<GameEvent> event) {
        if (player != null) {
            stack.causeUseVibration(player, event);
        }
    }

    @Redirect(method = "useOn", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/item/ItemStack;causeUseVibration(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder$Reference;)V"))
    private void silkard$vibrationWater(ItemStack stack, Entity player, Holder.Reference<GameEvent> event) {
        if (player != null) {
            stack.causeUseVibration(player, event);
        }
    }
}
