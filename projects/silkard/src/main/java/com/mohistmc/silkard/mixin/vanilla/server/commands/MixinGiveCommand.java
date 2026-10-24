package com.mohistmc.silkard.mixin.vanilla.server.commands;

import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(GiveCommand.class)
public class MixinGiveCommand {

    // CraftBukkit - SPIGOT-2942: 26.3 的 giveItem 不再调 ServerPlayer.drop，改走 createItemStackToDrop，@At 目标待重定位
    @Redirect(method = "giveItem", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;createItemStackToDrop(Lnet/minecraft/world/item/ItemStack;ZLnet/minecraft/util/Prediction;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private static ItemEntity silkard$createItemStackToDrop(ServerPlayer player, ItemStack stack, boolean dropAround, Object prediction) {
        return player.drop(stack, dropAround, false, false); // CraftBukkit - SPIGOT-2942: Add boolean to call event
    }
}
