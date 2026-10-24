package com.mohistmc.silkard.mixin.vanilla.world.item.crafting;

import com.mohistmc.silkard.injected.world.item.crafting.ContextRecipe;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.TransmuteRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftTransmuteRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(TransmuteRecipe.class)
public abstract class MixinTransmuteRecipe implements ContextRecipe {

    @Shadow
    @Final
    private Ingredient input;

    @Shadow
    @Final
    private Ingredient material;

    @Shadow
    @Final
    private boolean addMaterialCountToResult;

    @Shadow
    private SlotDisplay resultDisplay(int resultCount) {
        throw new AssertionError();
    }

    @Shadow
    private int minMaterialCount() {
        throw new AssertionError();
    }

    @Shadow
    private int maxMaterialCount() {
        throw new AssertionError();
    }

    // CraftBukkit start
    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        return new CraftTransmuteRecipe(id, CraftItemStack.asCraftMirror(this.resultDisplay(1).resolveForFirstStack(ContextMap.EMPTY)), CraftRecipe.toBukkit(this.input), CraftRecipe.toBukkit(this.material), this.minMaterialCount(), this.maxMaterialCount(), this.addMaterialCountToResult);
    }
    // CraftBukkit end
}
