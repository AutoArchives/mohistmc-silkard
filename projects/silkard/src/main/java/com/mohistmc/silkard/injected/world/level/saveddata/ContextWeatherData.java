package com.mohistmc.silkard.injected.world.level.saveddata;

import com.mohistmc.silkard.util.ContextStateException;
import net.minecraft.server.level.ServerLevel;

public interface ContextWeatherData {

    default void silkard$init(ServerLevel world) {
        throw new ContextStateException("Not implemented");
    }
}
