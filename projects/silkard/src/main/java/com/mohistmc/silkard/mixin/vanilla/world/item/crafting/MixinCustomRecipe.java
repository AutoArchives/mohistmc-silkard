package com.mohistmc.silkard.mixin.vanilla.world.item.crafting;

import com.mohistmc.silkard.injected.world.item.crafting.ContextRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftComplexRecipe;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(CustomRecipe.class)
public abstract class MixinCustomRecipe implements ContextRecipe {

    // CraftBukkit start
    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        CustomRecipe handle = (CustomRecipe) (Object) this;
        CraftItemStack result = CraftItemStack.asCraftMirror(ItemStack.EMPTY);
        CraftComplexRecipe recipe = new CraftComplexRecipe(id, result, handle);
        recipe.setGroup(handle.group());
        recipe.setCategory(CraftRecipe.getCategory(handle.category()));
        return recipe;
    }
    // CraftBukkit end
}
