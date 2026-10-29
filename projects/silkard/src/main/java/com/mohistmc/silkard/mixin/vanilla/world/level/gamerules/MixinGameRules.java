package com.mohistmc.silkard.mixin.vanilla.world.level.gamerules;

import com.mohistmc.silkard.injected.world.level.gamerules.ContextGameRules;
import java.util.Objects;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.waypoints.ServerWaypointManager;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(GameRules.class)
public class MixinGameRules implements ContextGameRules {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    private GameRuleMap rules;

    // CraftBukkit start - per-world
    @Override
    public <T> void set(GameRule<T> gameRule, T value, @Nullable ServerLevel server) {
        if (!this.rules.has(gameRule)) {
            LOGGER.warn("Tried to set invalid game rule '{}' to value '{}'", gameRule.getIdentifierWithFallback(), value);
        } else {
            this.rules.set(gameRule, value);
            if (server != null) {
                silkard$onGameRuleChanged(gameRule, value, server);
            }
        }
    }

    @Override
    public void setAll(GameRules other, @Nullable ServerLevel server) {
        this.setAll(((MixinGameRules) (Object) other).rules, server);
    }

    @Override
    public void setAll(GameRuleMap gameRulesMap, @Nullable ServerLevel server) {
        gameRulesMap.keySet().forEach((gamerule) -> {
            this.setFromOther(gameRulesMap, gamerule, server);
        });
    }

    @Override
    public <T> void setFromOther(GameRuleMap gameRulesMap, GameRule<T> gameRule, @Nullable ServerLevel server) {
        this.set(gameRule, Objects.requireNonNull(gameRulesMap.get(gameRule)), server);
    }

    private static <T> void silkard$onGameRuleChanged(GameRule<T> rule, T value, ServerLevel serverlevel) {
        serverlevel.getServer().notificationManager().onGameRuleChanged(rule, value);
        if (rule == GameRules.REDUCED_DEBUG_INFO) {
            byte b0 = (byte) ((Boolean) value ? 22 : 23);

            for (ServerPlayer serverplayer : serverlevel.players()) {
                serverplayer.connection.send(new ClientboundEntityEventPacket(serverplayer, b0));
            }
        } else if (rule != GameRules.LIMITED_CRAFTING && rule != GameRules.IMMEDIATE_RESPAWN) {
            if (rule == GameRules.LOCATOR_BAR) {
                ServerWaypointManager serverwaypointmanager = serverlevel.getWaypointManager();

                if ((Boolean) value) {
                    serverlevel.players().forEach(serverwaypointmanager::updatePlayer);
                } else {
                    serverwaypointmanager.breakAllConnections();
                }
            } else if (rule == GameRules.ADVANCE_TIME) {
                net.minecraft.network.protocol.game.ClientboundSetTimePacket packet = serverlevel.clockManager().createFullSyncPacket();
                serverlevel.players().forEach((player) -> player.connection.send(packet));
            }
        } else {
            ClientboundGameEventPacket.Type clientboundgameeventpacket_type = rule == GameRules.LIMITED_CRAFTING ? ClientboundGameEventPacket.LIMITED_CRAFTING : ClientboundGameEventPacket.IMMEDIATE_RESPAWN;
            ClientboundGameEventPacket clientboundgameeventpacket = new ClientboundGameEventPacket(clientboundgameeventpacket_type, (Boolean) value ? 1.0F : 0.0F);

            serverlevel.players().forEach((serverplayer1) -> {
                serverplayer1.connection.send(clientboundgameeventpacket);
            });
        }
    }
    // CraftBukkit end
}
