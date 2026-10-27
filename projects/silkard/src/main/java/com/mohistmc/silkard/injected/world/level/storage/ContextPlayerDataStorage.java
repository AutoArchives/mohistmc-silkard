package com.mohistmc.silkard.injected.world.level.storage;

import com.mohistmc.silkard.util.ContextStateException;
import java.io.File;
import java.util.Optional;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;

public interface ContextPlayerDataStorage {

    default CompoundTag getPlayerData(String s) {
        throw new ContextStateException("Not implemented");
    }

    default File getPlayerDir() {
        throw new ContextStateException("Not implemented");
    }

    default Optional<CompoundTag> load(String name, String s1, String s) { // name, uuid, extension
        return Optional.empty();
    }

    default Optional<CompoundTag> load(String name, String uuid) {
        return Optional.empty();
    }

    default void backup(String name, String s1, String s) {
        throw new ContextStateException("Not implemented");
    }

    default Optional<ValueInput> load(String name, String string, ProblemReporter discarding, RegistryAccess.Frozen frozen) {
        throw new ContextStateException("Not implemented");
    }

    default Optional<CompoundTag> load(Player player) {
        throw new ContextStateException("Not implemented");
    }
}
