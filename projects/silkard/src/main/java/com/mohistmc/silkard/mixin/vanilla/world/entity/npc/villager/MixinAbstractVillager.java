package com.mohistmc.silkard.mixin.vanilla.world.entity.npc.villager;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.TradeSet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.inventory.CraftMerchantRecipe;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(AbstractVillager.class)
public class MixinAbstractVillager {

    @Unique
    private int silkard$offerMark;

    // CraftBukkit start
    public org.bukkit.craftbukkit.inventory.CraftMerchant getCraftMerchant() {
        return (org.bukkit.craftbukkit.inventory.CraftMerchant) ((Entity) (Object) this).getBukkitEntity();
    }
    // CraftBukkit end

    // CraftBukkit start - call VillagerAcquireTradeEvent
    @Inject(method = "addOffersFromTradeSet", at = @At("HEAD"))
    private void silkard$markOffers(ServerLevel level, MerchantOffers offers, ResourceKey<TradeSet> key, CallbackInfo ci) {
        this.silkard$offerMark = offers.size();
    }

    @Inject(method = "addOffersFromTradeSet", at = @At("RETURN"))
    private void silkard$acquireTrade(ServerLevel level, MerchantOffers offers, ResourceKey<TradeSet> key, CallbackInfo ci) {
        for (int i = offers.size() - 1; i >= this.silkard$offerMark; i--) {
            MerchantOffer offer = offers.get(i);
            VillagerAcquireTradeEvent event = new VillagerAcquireTradeEvent((org.bukkit.entity.AbstractVillager) ((Entity) (Object) this).getBukkitEntity(), offer.asBukkit());
            if (((ContextEntity) (Object) this).silkard_valid()) {
                Bukkit.getPluginManager().callEvent(event);
            }
            if (event.isCancelled()) {
                offers.remove(i);
            } else {
                offers.set(i, CraftMerchantRecipe.fromBukkit(event.getRecipe()).toMinecraft());
            }
        }
    }
    // CraftBukkit end
}
