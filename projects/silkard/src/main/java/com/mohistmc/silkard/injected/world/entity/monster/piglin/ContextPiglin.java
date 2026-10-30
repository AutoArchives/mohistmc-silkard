package com.mohistmc.silkard.injected.world.entity.monster.piglin;

import com.mohistmc.silkard.util.ContextStateException;
import java.util.Set;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface ContextPiglin {

    default void silkard$holdInMainHand(ItemStack stack) {
        throw new ContextStateException("Not implemented");
    }

    default Set<Item> silkard$allowedBarterItems() {
        throw new ContextStateException("Not implemented");
    }

    default Set<Item> silkard$interestItems() {
        throw new ContextStateException("Not implemented");
    }
}
