package com.mohistmc.silkard.injected.network;

import com.mohistmc.silkard.util.ContextStateException;
import io.netty.buffer.ByteBuf;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
public interface ContextDiscardedPayload {

    default ByteBuf silkard$data() {
        throw new ContextStateException("Not implemented");
    }

    default void silkard$data(ByteBuf data) {
        throw new ContextStateException("Not implemented");
    }
}
