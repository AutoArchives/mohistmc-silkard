package org.bukkit.craftbukkit.util;

import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public final class Versioning {

    // Silkard - the mod version is "<minecraft>-<abbreviated git id>", the git id is the one we report
    public static String getGitHash() {
        try {
            Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer("silkard");
            String version = mod.map(container -> container.getMetadata().getVersion().getFriendlyString()).orElse("unknown");
            int separator = version.lastIndexOf('-');
            return separator < 0 ? version : version.substring(separator + 1);
        } catch (Throwable ex) {
            return "unknown";
        }
    }

    public static String getBukkitVersion(String minecraftVersion) {
        return minecraftVersion + "-R0.1-SNAPSHOT";
    }
}
