package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.panda;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.item.ItemEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(Panda.class)
public class MixinPanda {

    @Inject(method = "pickUpItem", at = @At("HEAD"), cancellable = true)
    private void silkard$pickUpItem(ServerLevel level, ItemEntity entity, CallbackInfo ci) {
        Panda self = (Panda) (Object) this;
        if (CraftEventFactory.callEntityPickupItemEvent(self, entity, 0, !self.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()).isCancelled()) {
            ci.cancel();
        }
    }

    @Redirect(method = "pickUpItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void silkard$pickUpItemDiscard(ItemEntity entity) {
        entity.discard(EntityRemoveEvent.Cause.PICKUP);
    }
}
