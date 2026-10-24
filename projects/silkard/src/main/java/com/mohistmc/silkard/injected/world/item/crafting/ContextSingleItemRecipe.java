package com.mohistmc.silkard.injected.world.item.crafting;

import com.mohistmc.silkard.util.ContextStateException;
import net.minecraft.world.item.ItemStackTemplate;

public interface ContextSingleItemRecipe {

    default ItemStackTemplate silkard$result() {
        throw new ContextStateException("Not implemented");
    }
}
