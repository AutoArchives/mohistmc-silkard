package com.mohistmc.silkard.mixin.vanilla.world.item.crafting;

import com.mohistmc.silkard.injected.world.item.crafting.ContextRecipe;
import com.mohistmc.silkard.injected.world.item.crafting.ContextSingleItemRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftBlastingRecipe;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(BlastingRecipe.class)
public abstract class MixinBlastingRecipe implements ContextRecipe, ContextSingleItemRecipe {

    // CraftBukkit start
    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        BlastingRecipe handle = (BlastingRecipe) (Object) this;
        CraftItemStack result = CraftItemStack.asCraftMirror(this.silkard$result());

        CraftBlastingRecipe recipe = new CraftBlastingRecipe(id, result, CraftRecipe.toBukkit(handle.input()), handle.experience(), handle.cookingTime());
        recipe.setGroup(handle.group());
        recipe.setCategory(CraftRecipe.getCategory(handle.category()));

        return recipe;
    }
    // CraftBukkit end
}
