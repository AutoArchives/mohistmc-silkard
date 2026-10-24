package com.mohistmc.silkard.injected.world.item.crafting;

import com.mohistmc.silkard.util.ContextStateException;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public interface ContextRecipeMap {

    // CraftBukkit start - mutable, ordered
    default void silkard$makeMutable() {
        throw new ContextStateException("Not implemented");
    }

    default void addRecipe(RecipeHolder<?> irecipe) {
        throw new ContextStateException("Not implemented");
    }

    default boolean removeRecipe(ResourceKey<Recipe<?>> mcKey) {
        throw new ContextStateException("Not implemented");
    }
    // CraftBukkit end
}
