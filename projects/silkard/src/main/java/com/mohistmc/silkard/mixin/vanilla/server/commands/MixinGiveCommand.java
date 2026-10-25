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

    // CraftBukkit - SPIGOT-2942: redirect the item drop so Player#drop fires the event with extra flags
    @Redirect(method = "giveItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;createItemStackToDrop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private static ItemEntity silkard$createItemStackToDrop(ServerPlayer player, ItemStack stack, boolean dropAround, boolean includeThrower) {
        return player.drop(stack, dropAround, false, false); // CraftBukkit - SPIGOT-2942: Add boolean to call event
    }
}
