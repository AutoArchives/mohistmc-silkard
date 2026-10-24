package com.mohistmc.silkard.injected.server;

import com.mohistmc.silkard.util.ContextStateException;
import net.minecraft.server.dedicated.DedicatedServer;

public interface ContextDedicatedServer {

    default boolean silkard$bukkitInit() {
        throw new ContextStateException("Not implemented");
    }

    default DedicatedServer self() {
        throw new ContextStateException("Not implemented");
    }
}
