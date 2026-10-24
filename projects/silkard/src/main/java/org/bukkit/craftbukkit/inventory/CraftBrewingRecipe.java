package org.bukkit.craftbukkit.inventory;

import com.mohistmc.silkard.bukkit.BukkitUtils;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BrewingRecipe;
import org.bukkit.inventory.ItemStack;

public class CraftBrewingRecipe extends BrewingRecipe implements CraftRecipe {

    private final net.minecraft.world.item.crafting.BrewingRecipe recipe;

    public CraftBrewingRecipe(NamespacedKey key, ItemStack result, net.minecraft.world.item.crafting.BrewingRecipe recipe) {
        super(key, result);
        this.recipe = recipe;
    }

    public static CraftBrewingRecipe fromBukkitRecipe(BrewingRecipe recipe) {
        if (recipe instanceof CraftBrewingRecipe) {
            return (CraftBrewingRecipe) recipe;
        }
        throw new UnsupportedOperationException("Cannot add custom brewing recipe");
    }

    @Override
    public void addToCraftingManager() {
        BukkitUtils.getServer().getRecipeManager().addRecipe(new RecipeHolder<>(CraftRecipe.toMinecraft(this.getKey()), recipe));
    }
}
