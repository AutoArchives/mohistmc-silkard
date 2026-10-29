package com.mohistmc.silkard.injected.world.item.component;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface ContextConsumable {

    default void cancelUsingItem(ServerPlayer player, ItemStack stack) {
    }
}
