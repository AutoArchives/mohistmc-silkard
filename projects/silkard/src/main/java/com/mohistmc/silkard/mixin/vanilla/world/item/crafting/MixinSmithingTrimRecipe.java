package com.mohistmc.silkard.mixin.vanilla.world.item.crafting;

import com.mohistmc.silkard.injected.world.item.crafting.ContextRecipe;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftSmithingTrimRecipe;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimPattern;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(SmithingTrimRecipe.class)
public abstract class MixinSmithingTrimRecipe implements ContextRecipe {

    @Shadow
    @Final
    private Ingredient template;

    @Shadow
    @Final
    private Ingredient base;

    @Shadow
    @Final
    private Ingredient addition;

    @Shadow
    @Final
    private Holder<net.minecraft.world.item.equipment.trim.TrimPattern> pattern;

    // CraftBukkit start
    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        return new CraftSmithingTrimRecipe(id, CraftRecipe.toBukkit(this.template), CraftRecipe.toBukkit(this.base), CraftRecipe.toBukkit(this.addition), CraftTrimPattern.minecraftHolderToBukkit(this.pattern));
    }
    // CraftBukkit end
}
