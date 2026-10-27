package com.mohistmc.silkard.mixin.vanilla.server.network;

import com.mohistmc.silkard.injected.server.network.ContextServerConnectionListener;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import java.net.InetAddress;
import java.util.List;
import net.minecraft.server.network.ServerConnectionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ServerConnectionListener.class)
public class MixinServerConnectionListener implements ContextServerConnectionListener {

    @Shadow
    private List<ChannelFuture> channels;

    @Redirect(method = "startTcpServerListener", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/ServerBootstrap;localAddress(Ljava/net/InetAddress;I)Lio/netty/bootstrap/AbstractBootstrap;"))
    private AbstractBootstrap silkard$holdIncomingConnections(ServerBootstrap bootstrap, InetAddress address, int port) {
        // CraftBukkit - stop reading until the server finished loading, see acceptConnections()
        bootstrap.option(ChannelOption.AUTO_READ, false);
        return bootstrap.localAddress(address, port);
    }

    @Override
    public void acceptConnections() {
        synchronized (this.channels) {
            for (ChannelFuture future : this.channels) {
                future.channel().config().setAutoRead(true);
            }
        }
    }
}
