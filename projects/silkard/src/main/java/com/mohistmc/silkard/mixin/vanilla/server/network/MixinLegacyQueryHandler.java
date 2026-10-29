package com.mohistmc.silkard.mixin.vanilla.server.network;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.Locale;
import net.minecraft.server.ServerInfo;
import net.minecraft.server.network.LegacyQueryHandler;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.server.ServerListPingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(LegacyQueryHandler.class)
public class MixinLegacyQueryHandler {

    @Shadow
    private ServerInfo server;

    @Unique
    private ServerListPingEvent silkard$legacyPingEvent;

    // CraftBukkit start
    @Inject(method = "channelRead", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/LegacyQueryHandler;createVersion0Response(Lnet/minecraft/server/ServerInfo;)Ljava/lang/String;", shift = At.Shift.BEFORE))
    private void silkard$version0Ping(io.netty.channel.ChannelHandlerContext ctx, Object msg, CallbackInfo ci, @Local java.net.SocketAddress address) {
        this.silkard$legacyPingEvent = CraftEventFactory.callServerListPingEvent(address, this.server.getMotd(), this.server.getPlayerCount(), this.server.getMaxPlayers());
    }

    @Redirect(method = "channelRead", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/LegacyQueryHandler;createVersion0Response(Lnet/minecraft/server/ServerInfo;)Ljava/lang/String;"))
    private String silkard$version0Response(ServerInfo server) {
        ServerListPingEvent event = this.silkard$legacyPingEvent;
        return String.format(Locale.ROOT, "%s\u00a7%d\u00a7%d", event.getMotd(), event.getNumPlayers(), event.getMaxPlayers());
    }

    @Inject(method = "channelRead", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/LegacyQueryHandler;createVersion1Response(Lnet/minecraft/server/ServerInfo;)Ljava/lang/String;", shift = At.Shift.BEFORE))
    private void silkard$version1Ping(io.netty.channel.ChannelHandlerContext ctx, Object msg, CallbackInfo ci, @Local java.net.SocketAddress address) {
        this.silkard$legacyPingEvent = CraftEventFactory.callServerListPingEvent(address, this.server.getMotd(), this.server.getPlayerCount(), this.server.getMaxPlayers());
    }

    @Redirect(method = "channelRead", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/LegacyQueryHandler;createVersion1Response(Lnet/minecraft/server/ServerInfo;)Ljava/lang/String;"))
    private String silkard$version1Response(ServerInfo server) {
        ServerListPingEvent event = this.silkard$legacyPingEvent;
        return String.format(Locale.ROOT, "\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, this.server.getServerVersion(), event.getMotd(), event.getNumPlayers(), event.getMaxPlayers());
    }
    // CraftBukkit end
}
