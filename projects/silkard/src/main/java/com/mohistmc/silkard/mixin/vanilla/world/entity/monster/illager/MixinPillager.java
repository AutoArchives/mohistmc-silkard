package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.illager;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.illager.Pillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Pillager.class)
public class MixinPillager {

    @Redirect(method = "pickUpItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void silkard_discard(ItemEntity entity) {
        entity.discard(org.bukkit.event.entity.EntityRemoveEvent.Cause.PICKUP);
    }
}
