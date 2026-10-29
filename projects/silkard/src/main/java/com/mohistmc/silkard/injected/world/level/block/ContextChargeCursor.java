package com.mohistmc.silkard.injected.world.level.block;

import com.mohistmc.silkard.util.ContextStateException;

public interface ContextChargeCursor {

    default void silkard$setCharge(int charge) {
        throw new ContextStateException("Not implemented");
    }
}
