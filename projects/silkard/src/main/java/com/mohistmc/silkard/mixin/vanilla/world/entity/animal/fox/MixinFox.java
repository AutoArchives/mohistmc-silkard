package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.fox;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(Fox.class)
public class MixinFox {

    // CraftBukkit start - fire EntityPickupItemEvent
    @Inject(method = "pickUpItem", at = @At("HEAD"), cancellable = true)
    private void silkard$pickupEvent(ServerLevel level, ItemEntity entity, CallbackInfo ci) {
        ItemStack itemstack = entity.getItem();
        if (CraftEventFactory.callEntityPickupItemEvent((Fox) (Object) this, entity, itemstack.getCount() - 1, !((Fox) (Object) this).canHoldItem(itemstack)).isCancelled()) {
            ci.cancel();
        }
    }
    // CraftBukkit end

    // CraftBukkit start - add Bukkit remove cause
    @Redirect(method = "pickUpItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void silkard$discard(ItemEntity self) {
        ((ContextEntity) (Object) this).discard(EntityRemoveEvent.Cause.PICKUP);
    }
    // CraftBukkit end
}
