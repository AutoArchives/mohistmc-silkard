package com.mohistmc.silkard.mixin.vanilla.world.item.crafting;

import com.mohistmc.silkard.injected.world.item.crafting.ContextSingleItemRecipe;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(SingleItemRecipe.class)
public abstract class MixinSingleItemRecipe implements ContextSingleItemRecipe {

    @Shadow
    @Final
    private ItemStackTemplate result;

    // CraftBukkit start
    @Override
    public ItemStackTemplate silkard$result() {
        return this.result;
    }
    // CraftBukkit end
}
