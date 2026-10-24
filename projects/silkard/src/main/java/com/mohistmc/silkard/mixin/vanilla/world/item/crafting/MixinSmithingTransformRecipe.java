package com.mohistmc.silkard.mixin.vanilla.world.item.crafting;

import com.mohistmc.silkard.injected.world.item.crafting.ContextRecipe;
import java.util.Optional;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftSmithingTransformRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(SmithingTransformRecipe.class)
public abstract class MixinSmithingTransformRecipe implements ContextRecipe {

    @Shadow
    @Final
    private Optional<Ingredient> template;

    @Shadow
    @Final
    private Ingredient base;

    @Shadow
    @Final
    private Optional<Ingredient> addition;

    @Shadow
    @Final
    private ItemStackTemplate result;

    // CraftBukkit start
    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        org.bukkit.inventory.ItemStack result = CraftItemStack.asCraftMirror(this.result);
        CraftSmithingTransformRecipe recipe = new CraftSmithingTransformRecipe(id, result, CraftRecipe.toBukkit(this.template), CraftRecipe.toBukkit(this.base), CraftRecipe.toBukkit(this.addition));
        return recipe;
    }
    // CraftBukkit end
}
