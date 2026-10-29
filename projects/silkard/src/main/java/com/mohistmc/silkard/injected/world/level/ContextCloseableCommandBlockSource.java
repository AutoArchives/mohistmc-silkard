package com.mohistmc.silkard.injected.world.level;

public interface ContextCloseableCommandBlockSource {

    default void silkard$setClosed(boolean closed) {
        throw new IllegalStateException("Not implemented");
    }
}
