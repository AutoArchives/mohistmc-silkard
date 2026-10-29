package com.mohistmc.silkard.mixin.vanilla.server.players;

import com.mohistmc.silkard.injected.world.entity.player.ContextPlayer;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(SleepStatus.class)
public class MixinSleepStatus {

    @Shadow
    private int activePlayers;

    @Shadow
    private int sleepingPlayers;

    @Shadow
    public int sleepersNeeded(int sleepPercentageNeeded) {
        throw new AssertionError();
    }

    // CraftBukkit start
    @Overwrite
    public boolean areEnoughDeepSleeping(int sleepPercentageNeeded, List<ServerPlayer> players) {
        int j = (int) players.stream().filter(eh -> eh.isSleepingLongEnough() || ((ContextPlayer) eh).silkard$fauxSleeping()).count();
        boolean anyDeepSleep = players.stream().anyMatch(ServerPlayer::isSleepingLongEnough);

        return anyDeepSleep && j >= this.sleepersNeeded(sleepPercentageNeeded);
    }

    @Overwrite
    public boolean update(List<ServerPlayer> players) {
        int i = this.activePlayers;
        int j = this.sleepingPlayers;
        this.activePlayers = 0;
        this.sleepingPlayers = 0;
        boolean anySleep = false;

        for (ServerPlayer serverplayer : players) {
            if (!serverplayer.isSpectator()) {
                ++this.activePlayers;
                if (serverplayer.isSleeping() || ((ContextPlayer) serverplayer).silkard$fauxSleeping()) {
                    ++this.sleepingPlayers;
                }
                if (serverplayer.isSleeping()) {
                    anySleep = true;
                }
            }
        }

        return anySleep && (j > 0 || this.sleepingPlayers > 0) && (i != this.activePlayers || j != this.sleepingPlayers);
    }
    // CraftBukkit end
}
