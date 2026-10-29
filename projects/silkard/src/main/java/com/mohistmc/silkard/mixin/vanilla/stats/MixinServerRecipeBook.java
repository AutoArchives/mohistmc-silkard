package com.mohistmc.silkard.mixin.vanilla.stats;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.Set;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.ServerRecipeBook;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(ServerRecipeBook.class)
public class MixinServerRecipeBook {

    // CraftBukkit - PlayerRecipeListUpdateEvent
    @Redirect(method = "addRecipes", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    private boolean silkard$knownOrEvent(Set<Object> known, Object key, @Local(argsOnly = true) ServerPlayer player) {
        return known.contains(key) || !CraftEventFactory.handlePlayerRecipeListUpdateEvent(player, ((ResourceKey<?>) key).identifier());
    }

    // SPIGOT-4478 - player.connection is null during PlayerLoginEvent
    @Redirect(method = {"addRecipes", "removeRecipes"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void silkard$send(ServerGamePacketListenerImpl connection, Packet<?> packet) {
        if (connection != null) {
            connection.send(packet);
        }
    }
}
