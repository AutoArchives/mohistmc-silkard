package com.mohistmc.silkard.mixin.vanilla.world.entity.npc;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(InventoryCarrier.class)
public interface MixinInventoryCarrier {

    // CraftBukkit - EntityPickupItemEvent
    @Redirect(method = "pickUpItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private static void silkard$discard(ItemEntity itemEntity,
                                 @Local(argsOnly = true) Mob mob,
                                 @Local(ordinal = 0) ItemStack itemStack,
                                 @Local(ordinal = 0) SimpleContainer container) {
        SimpleContainer copy = new SimpleContainer(container.getContainerSize());
        for (int i = 0; i < container.getContainerSize(); i++) {
            copy.setItem(i, container.getItem(i));
        }
        ItemStack remaining = copy.addItem(itemStack);

        if (CraftEventFactory.callEntityPickupItemEvent(mob, itemEntity, remaining.getCount(), false).isCancelled()) {
            return;
        }
        itemEntity.discard(EntityRemoveEvent.Cause.PICKUP);
    }
}
