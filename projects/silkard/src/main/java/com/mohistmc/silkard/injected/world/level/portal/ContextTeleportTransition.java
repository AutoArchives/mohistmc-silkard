package com.mohistmc.silkard.injected.world.level.portal;

import org.bukkit.event.player.PlayerTeleportEvent;

public interface ContextTeleportTransition {

    default PlayerTeleportEvent.TeleportCause silkard$cause() {
        return PlayerTeleportEvent.TeleportCause.UNKNOWN;
    }

    default void silkard$cause(PlayerTeleportEvent.TeleportCause cause) {
    }
}
