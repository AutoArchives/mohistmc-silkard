package com.mohistmc.silkard.injected.server.network;

import com.mohistmc.silkard.util.ContextStateException;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;

public interface ContextServerLoginPacketListenerImpl {

    default void disconnect(String s) {
        throw new ContextStateException("Not implemented");
    }

    default void callPlayerPreLoginEvents(GameProfile gameprofile) throws Exception {
        throw new ContextStateException("Not implemented");
    }

    default Connection silkard$connection() {
        throw new ContextStateException("Not implemented");
    }

    default void silkard$setKickMessage(Component component) {
        throw new ContextStateException("Not implemented");
    }

    default Component silkard$kickMessage() {
        throw new ContextStateException("Not implemented");
    }
}
