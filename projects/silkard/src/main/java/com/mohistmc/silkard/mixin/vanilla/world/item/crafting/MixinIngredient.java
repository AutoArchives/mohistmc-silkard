package com.mohistmc.silkard.mixin.vanilla.world.item.crafting;

import com.mohistmc.silkard.injected.world.item.crafting.ContextIngredient;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/5/13 21:35
 */
@Mixin(Ingredient.class)
public class MixinIngredient implements ContextIngredient {

    @Unique
    @Nullable
    private List<ItemStack> itemStacks;

    @Override
    public boolean isExact() {
        return this.itemStacks != null;
    }

    @Override
    public void silkard$itemStacks(List<ItemStack> itemStacks) {
        this.itemStacks = itemStacks;
    }

    @Override
    public List<ItemStack> itemStacks() {
        return this.itemStacks;
    }

    // CraftBukkit start - exact choice
    @Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void silkard$testExact(ItemStack input, CallbackInfoReturnable<Boolean> cir) {
        if (this.isExact()) {
            for (ItemStack stack : this.itemStacks()) {
                if (stack.getItem() == input.getItem() && ItemStack.isSameItemSameComponents(input, stack)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "equals", at = @At("RETURN"), cancellable = true)
    private void silkard$equals(Object o, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && o instanceof Ingredient other) {
            cir.setReturnValue(Objects.equals(this.itemStacks, ((ContextIngredient) other).itemStacks()));
        }
    }
    // CraftBukkit end
}
