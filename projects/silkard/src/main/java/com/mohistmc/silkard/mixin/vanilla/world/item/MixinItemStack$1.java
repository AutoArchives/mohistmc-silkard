package com.mohistmc.silkard.mixin.vanilla.world.item;

import com.mohistmc.silkard.injected.world.item.ContextItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(targets = "net.minecraft.world.item.ItemStack$1")
public class MixinItemStack$1 {

    // CraftBukkit - apply item meta after decoding components
    @Inject(method = "decode", at = @At("RETURN"), cancellable = true)
    private void silkard$setItemMeta(RegistryFriendlyByteBuf input, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        if (stack != null && !((ContextItemStack) stack).asPatch().isEmpty()) {
            CraftItemStack.setItemMeta(stack, CraftItemStack.getItemMeta(stack));
        }
    }
}
