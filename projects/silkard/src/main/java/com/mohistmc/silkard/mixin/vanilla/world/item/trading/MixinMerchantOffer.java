package com.mohistmc.silkard.mixin.vanilla.world.item.trading;

import com.mohistmc.silkard.injected.world.item.trading.ContextMerchantOffer;
import net.minecraft.world.item.trading.MerchantOffer;
import org.bukkit.craftbukkit.inventory.CraftMerchantRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(MerchantOffer.class)
public class MixinMerchantOffer implements ContextMerchantOffer {

    // CraftBukkit start
    @Unique
    private CraftMerchantRecipe bukkitHandle;

    @Override
    public CraftMerchantRecipe asBukkit() {
        return (this.bukkitHandle == null) ? this.bukkitHandle = new CraftMerchantRecipe((MerchantOffer) (Object) this) : this.bukkitHandle;
    }
    // CraftBukkit end
}
