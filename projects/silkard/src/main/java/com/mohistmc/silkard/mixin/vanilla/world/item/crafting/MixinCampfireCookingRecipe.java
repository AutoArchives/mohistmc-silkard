package com.mohistmc.silkard.mixin.vanilla.world.item.crafting;

import com.mohistmc.silkard.injected.world.item.crafting.ContextRecipe;
import com.mohistmc.silkard.injected.world.item.crafting.ContextSingleItemRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftCampfireRecipe;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(CampfireCookingRecipe.class)
public abstract class MixinCampfireCookingRecipe implements ContextRecipe, ContextSingleItemRecipe {

    // CraftBukkit start
    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        CampfireCookingRecipe handle = (CampfireCookingRecipe) (Object) this;
        CraftItemStack result = CraftItemStack.asCraftMirror(this.silkard$result());

        CraftCampfireRecipe recipe = new CraftCampfireRecipe(id, result, CraftRecipe.toBukkit(handle.input()), handle.experience(), handle.cookingTime());
        recipe.setGroup(handle.group());
        recipe.setCategory(CraftRecipe.getCategory(handle.category()));

        return recipe;
    }
    // CraftBukkit end
}
