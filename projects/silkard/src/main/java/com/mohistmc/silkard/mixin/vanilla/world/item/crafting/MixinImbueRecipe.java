package com.mohistmc.silkard.mixin.vanilla.world.item.crafting;

import com.mohistmc.silkard.injected.world.item.crafting.ContextRecipe;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.ImbueRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftImbueRecipe;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(ImbueRecipe.class)
public abstract class MixinImbueRecipe implements ContextRecipe {

    @Shadow
    @Final
    private net.minecraft.world.item.crafting.Ingredient source;

    @Shadow
    @Final
    private net.minecraft.world.item.crafting.Ingredient material;

    @Shadow
    @Final
    private ItemStackTemplate result;

    // CraftBukkit start
    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        return new CraftImbueRecipe(id, CraftItemStack.asCraftMirror(this.result), CraftRecipe.toBukkit(this.source), CraftRecipe.toBukkit(this.material));
    }
    // CraftBukkit end
}
