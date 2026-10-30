package com.mohistmc.silkard.mixin.vanilla.world.entity.raid;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.raid.Raider;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(Raider.class)
public class MixinRaider {

    // CraftBukkit start - add Bukkit remove cause
    @Redirect(method = "pickUpItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void silkard$discardPickedUp(ItemEntity itemEntity, ServerLevel level, ItemEntity pickup) {
        ((ContextEntity) (Object) itemEntity).discard(EntityRemoveEvent.Cause.PICKUP);
    }
    // CraftBukkit end
}
