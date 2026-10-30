package com.mohistmc.silkard.injected.world.level.storage;

import com.mohistmc.silkard.util.ContextStateException;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.LevelStem;

public interface ContextPrimaryLevelData {

    default void silkard$pdc(Tag pdc) {
        throw new ContextStateException("Not implemented");
    }

    default Registry<LevelStem> silkard_customDimensions() {
        throw new ContextStateException("Not implemented");
    }

    default void silkard_customDimensions(Registry<LevelStem> customDimensions) {
        throw new ContextStateException("Not implemented");
    }

    default void checkName(String name) {
        throw new ContextStateException("Not implemented");
    }

    default void setWorld(ServerLevel world) {
        throw new ContextStateException("Not implemented");
    }
}
