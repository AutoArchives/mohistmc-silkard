package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.dolphin;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.item.ItemEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(Dolphin.class)
public class MixinDolphin {

    // CraftBukkit start - fire EntityPickupItemEvent
    @Inject(method = "pickUpItem", at = @At("HEAD"), cancellable = true)
    private void silkard$pickupEvent(ServerLevel level, ItemEntity entity, CallbackInfo ci) {
        if (CraftEventFactory.callEntityPickupItemEvent((Dolphin) (Object) this, entity, 0, false).isCancelled()) {
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

    // CraftBukkit start - SPIGOT-6907: re-implement LivingEntity#setMaximumAir()
    @Overwrite
    public int getMaxAirSupply() {
        return ((ContextEntity) (Object) this).silkard_maxAirTicks();
    }
    // CraftBukkit end
}
