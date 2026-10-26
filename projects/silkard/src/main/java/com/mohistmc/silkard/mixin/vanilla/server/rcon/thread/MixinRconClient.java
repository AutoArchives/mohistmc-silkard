package com.mohistmc.silkard.mixin.vanilla.server.rcon.thread;

import com.mohistmc.silkard.injected.server.rcon.ContextRconConsoleSource;
import java.net.Socket;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.server.rcon.thread.RconClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RconClient.class)
public abstract class MixinRconClient {

    @Unique
    private RconConsoleSource silkard$rconConsoleSource;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void silkard$initRconSource(ServerInterface serverInterface, String rconPassword, Socket socket, CallbackInfo ci) {
        // CraftBukkit - per-client source so the bukkit sender carries the remote socket address
        this.silkard$rconConsoleSource = new RconConsoleSource((DedicatedServer) serverInterface);
        this.silkard$rconConsoleSource.silkard$setSocketAddress(socket.getRemoteSocketAddress());
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerInterface;runCommand(Ljava/lang/String;)Ljava/lang/String;"))
    private String silkard$runCommand(ServerInterface serverInterface, String command) {
        // CraftBukkit - RemoteServerCommandEvent + bukkit command map dispatch
        return ((DedicatedServer) serverInterface).silkard$runCommand(this.silkard$rconConsoleSource, command);
    }
}
