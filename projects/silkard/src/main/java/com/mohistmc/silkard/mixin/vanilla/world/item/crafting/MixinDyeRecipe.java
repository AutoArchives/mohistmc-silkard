package com.mohistmc.silkard.mixin.vanilla.world.item.crafting;

import com.mohistmc.silkard.injected.world.item.crafting.ContextRecipe;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.DyeRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftDyeRecipe;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(DyeRecipe.class)
public abstract class MixinDyeRecipe implements ContextRecipe {

    @Shadow
    @Final
    private net.minecraft.world.item.crafting.Ingredient target;

    @Shadow
    @Final
    private net.minecraft.world.item.crafting.Ingredient dye;

    @Shadow
    @Final
    private ItemStackTemplate result;

    // CraftBukkit start
    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        return new CraftDyeRecipe(id, CraftItemStack.asCraftMirror(this.result), CraftRecipe.toBukkit(this.target), CraftRecipe.toBukkit(this.dye));
    }
    // CraftBukkit end
}
