package com.mohistmc.silkard.mixin.vanilla.world.item.crafting;

import com.mohistmc.silkard.injected.world.item.crafting.ContextShapelessRecipe;
import java.util.List;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftShapelessRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ShapelessRecipe.class)
public abstract class MixinShapelessRecipe implements ContextShapelessRecipe {

    @Shadow
    @Final
    private ItemStackTemplate result;

    @Shadow
    @Final
    private List<Ingredient> ingredients;

    // CraftBukkit start
    @SuppressWarnings("unchecked")
    @Override
    public org.bukkit.inventory.ShapelessRecipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
        CraftShapelessRecipe recipe = new CraftShapelessRecipe(id, result, (ShapelessRecipe) (Object) this);
        recipe.setGroup(((ShapelessRecipe) (Object) this).group());
        recipe.setCategory(CraftRecipe.getCategory(((ShapelessRecipe) (Object) this).category()));

        for (Ingredient list : this.ingredients) {
            recipe.addIngredient(CraftRecipe.toBukkit(list));
        }
        return recipe;
    }
    // CraftBukkit end
}
