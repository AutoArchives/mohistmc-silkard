package com.mohistmc.silkard.mixin.vanilla.network;

import com.mohistmc.silkard.injected.network.ContextConnection;
import java.net.SocketAddress;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(Connection.class)
public abstract class MixinConnection implements ContextConnection {

    // CraftBukkit - add field (CB 自增字段，26.3 vanilla 无此成员，必须 @Unique 而非 @Shadow)
    @Unique
    public String hostname = "";

    @Shadow
    public abstract SocketAddress getRemoteAddress();

    @Override
    public String silkard$hostname() {
        return hostname;
    }

    @Override
    public void silkard$setHostName(String hostName) {
        this.hostname = hostName;
    }

    @Override
    public SocketAddress getRawAddress() {
        return getRemoteAddress();
    }
}
