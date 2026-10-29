package com.mohistmc.silkard.injected.server.level;

import com.mohistmc.silkard.bukkit.BukkitCallbackExecutor;
import com.mohistmc.silkard.util.ContextStateException;
import java.util.function.BooleanSupplier;

public interface ContextChunkMap {

    default BukkitCallbackExecutor silkard$callbackExecutor() {
        throw new ContextStateException("Not implemented");
    }

    default void silkard$tick(BooleanSupplier supplier) {
        throw new ContextStateException("Not implemented");
    }
}
