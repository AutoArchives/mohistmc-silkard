package com.mohistmc.silkard.mixin.vanilla.server.network;

import com.mohistmc.silkard.injected.server.ContextMinecraftServer;
import com.mohistmc.silkard.injected.server.network.ContextServerGamePacketListenerImpl;
import com.mojang.brigadier.ParseResults;
import java.util.Collections;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignableCommand;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.LazyPlayerSet;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl implements ContextServerGamePacketListenerImpl {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    public ServerPlayer player;

    // CraftBukkit - rewritten command stashed by the preprocess injectors, consumed by the redirects below
    @Unique
    private String silkard$unsignedCommand;

    @Unique
    private String silkard$signedCommand;

    @Unique
    private String silkard$signedOriginal;

    @Redirect(method = "forceSendPlayerSupportBlocks", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"))
    private void silkard$demoteStandingOnAirLog(Logger instance, String format, Object arg) {
        // CraftBukkit - SPIGOT-8156: warn -> debug
        instance.debug(format, arg);
    }

    @Shadow
    private ParseResults<CommandSourceStack> parseCommand(String command) {
        throw new AssertionError();
    }

    @Shadow
    private <S> Map<String, PlayerChatMessage> collectSignedArguments(ServerboundChatCommandSignedPacket packet, SignableCommand<S> signable, LastSeenMessages lastSeenMessages) throws net.minecraft.network.chat.SignedMessageChain.DecodeException {
        throw new AssertionError();
    }

    // CraftBukkit start - PlayerCommandPreprocessEvent
    @Inject(method = "performUnsignedChatCommand(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private void silkard$preprocessUnsigned(String command, CallbackInfo ci) {
        this.silkard$unsignedCommand = null;
        net.minecraft.server.MinecraftServer server = this.player.level().getServer();
        CraftServer craftServer = ((ContextMinecraftServer) server).silkard$server();
        if (craftServer == null) {
            return;
        }
        if (org.spigotmc.SpigotConfig.logCommands) LOGGER.info(this.player.getBukkitEntity().getName() + " issued server command: /" + command);
        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(this.player.getBukkitEntity(), "/" + command, new LazyPlayerSet(server));
        craftServer.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        this.silkard$unsignedCommand = event.getMessage().substring(1);
    }

    @Redirect(method = "performUnsignedChatCommand(Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;parseCommand(Ljava/lang/String;)Lcom/mojang/brigadier/ParseResults;"))
    private ParseResults<CommandSourceStack> silkard$parseUnsigned(String command) {
        return this.parseCommand(this.silkard$unsignedCommand != null ? this.silkard$unsignedCommand : command);
    }

    @Redirect(method = "performUnsignedChatCommand(Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performCommand(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)V"))
    private void silkard$performUnsigned(Commands commands, ParseResults<CommandSourceStack> results, String commandString) {
        commands.performCommand(results, this.silkard$unsignedCommand != null ? this.silkard$unsignedCommand : commandString);
    }

    @Inject(method = "performSignedChatCommand(Lnet/minecraft/network/protocol/game/ServerboundChatCommandSignedPacket;Lnet/minecraft/network/chat/LastSeenMessages;)V", at = @At("HEAD"), cancellable = true)
    private void silkard$preprocessSigned(ServerboundChatCommandSignedPacket packet, LastSeenMessages lastSeenMessages, CallbackInfo ci) {
        this.silkard$signedCommand = null;
        this.silkard$signedOriginal = null;
        net.minecraft.server.MinecraftServer server = this.player.level().getServer();
        CraftServer craftServer = ((ContextMinecraftServer) server).silkard$server();
        if (craftServer == null) {
            return;
        }
        String command = packet.command();
        if (org.spigotmc.SpigotConfig.logCommands) LOGGER.info(this.player.getBukkitEntity().getName() + " issued server command: /" + command);
        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(this.player.getBukkitEntity(), "/" + command, new LazyPlayerSet(server));
        craftServer.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        this.silkard$signedOriginal = command;
        this.silkard$signedCommand = event.getMessage().substring(1);
    }

    @Redirect(method = "performSignedChatCommand(Lnet/minecraft/network/protocol/game/ServerboundChatCommandSignedPacket;Lnet/minecraft/network/chat/LastSeenMessages;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ServerboundChatCommandSignedPacket;command()Ljava/lang/String;"))
    private String silkard$signedCommand(ServerboundChatCommandSignedPacket packet) {
        return this.silkard$signedCommand != null ? this.silkard$signedCommand : packet.command();
    }

    @Redirect(method = "performSignedChatCommand(Lnet/minecraft/network/protocol/game/ServerboundChatCommandSignedPacket;Lnet/minecraft/network/chat/LastSeenMessages;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;collectSignedArguments(Lnet/minecraft/network/protocol/game/ServerboundChatCommandSignedPacket;Lnet/minecraft/network/chat/SignableCommand;Lnet/minecraft/network/chat/LastSeenMessages;)Ljava/util/Map;"))
    private Map<String, PlayerChatMessage> silkard$collectSigned(ServerboundChatCommandSignedPacket packet, SignableCommand<CommandSourceStack> signable, LastSeenMessages lastSeenMessages) throws net.minecraft.network.chat.SignedMessageChain.DecodeException {
        // CraftBukkit - a plugin rewrote the command, the original signatures no longer match
        if (this.silkard$signedOriginal == null || this.silkard$signedOriginal.equals(this.silkard$signedCommand)) {
            return this.collectSignedArguments(packet, signable, lastSeenMessages);
        }
        return Collections.emptyMap();
    }
    // CraftBukkit end
}
