package com.mohistmc.silkard.mixin.vanilla.server.rcon;

import com.mohistmc.silkard.injected.server.rcon.ContextRconConsoleSource;
import java.net.SocketAddress;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.rcon.RconConsoleSource;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.CraftRemoteConsoleCommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(RconConsoleSource.class)
public class MixinRconConsoleSource implements ContextRconConsoleSource {

    @Shadow
    @Final
    private java.lang.StringBuffer buffer;

    @Unique
    private SocketAddress silkard$address;

    @Unique
    private CraftRemoteConsoleCommandSender silkard$remoteConsole;

    @Override
    public SocketAddress silkard$socketAddress() {
        return this.silkard$address;
    }

    @Override
    public void silkard$setSocketAddress(SocketAddress socketAddress) {
        this.silkard$address = socketAddress;
    }

    @Override
    public void sendMessage(String message) {
        this.buffer.append(message);
    }

    @Override
    public CommandSender getBukkitSender(CommandSourceStack wrapper) {
        if (this.silkard$remoteConsole == null) {
            this.silkard$remoteConsole = new CraftRemoteConsoleCommandSender((RconConsoleSource) (Object) this);
        }
        return this.silkard$remoteConsole;
    }
}
