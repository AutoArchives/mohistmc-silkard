package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.block.VaultDisplayItemEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/31
 */
@Mixin(targets = "net.minecraft.world.level.block.entity.vault.VaultBlockEntity$Server")
public class MixinVaultBlockEntity$Server {

    // CraftBukkit start - BlockDispenseLootEvent
    @ModifyExpressionValue(method = "tryInsertKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/vault/VaultBlockEntity$Server;resolveItemsToEject(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/block/entity/vault/VaultConfig;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;"))
    private static List<ItemStack> silkard$dispenseLoot(List<ItemStack> original, @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) Player player) {
        BlockDispenseLootEvent event = CraftEventFactory.callBlockDispenseLootEvent(level, pos, player, original);
        if (event.isCancelled()) {
            return List.of();
        }
        return event.getDispensedLoot().stream().map(CraftItemStack::asNMSCopy).toList();
    }
    // CraftBukkit end

    // CraftBukkit start - VaultDisplayItemEvent
    @Redirect(method = "cycleDisplayItemFromLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/vault/VaultSharedData;setDisplayItem(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 1))
    private static void silkard$displayItem(VaultSharedData sharedData, ItemStack displayItem, @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) BlockPos pos) {
        VaultDisplayItemEvent event = CraftEventFactory.callVaultDisplayItemEvent(level, pos, displayItem);
        if (!event.isCancelled()) {
            sharedData.setDisplayItem(CraftItemStack.asNMSCopy(event.getDisplayItem()));
        }
    }
    // CraftBukkit end
}
