package com.mohistmc.silkard.mixin.vanilla.server;

import java.util.List;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(ServerScoreboard.class)
public class MixinServerScoreboard {

    // CraftBukkit start - Send to players
    @Redirect(method = {"onScoreChanged", "onPlayerRemoved", "onPlayerScoreRemoved", "setDisplayObjective", "addPlayerToTeam", "removePlayerFromTeam", "onObjectiveChanged", "onTeamAdded", "onTeamChanged", "onTeamRemoved"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void silkard$broadcastAll(PlayerList playerList, Packet<?> packet) {
        for (ServerPlayer serverplayer : playerList.getPlayers()) {
            if (serverplayer.getBukkitEntity().getScoreboard().getHandle() == (Scoreboard) (Object) this) {
                serverplayer.connection.send(packet);
            }
        }
    }

    @Redirect(method = {"startTrackingObjective", "stopTrackingObjective"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;getPlayers()Ljava/util/List;"))
    private List<ServerPlayer> silkard$trackingPlayers(PlayerList playerList) {
        // Only players on this board
        return playerList.getPlayers().stream().filter(serverplayer -> serverplayer.getBukkitEntity().getScoreboard().getHandle() == (Scoreboard) (Object) this).toList();
    }
    // CraftBukkit end
}
