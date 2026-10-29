package com.mohistmc.silkard.mixin.vanilla.server.level;

import com.mohistmc.silkard.injected.server.level.ContextServerPlayer;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.player.PlayerSpawnChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import com.mohistmc.silkard.injected.world.level.portal.ContextTeleportTransition;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.level.portal.TeleportTransition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/5/13 17:07
 */
@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player implements ContextServerPlayer {

    // @formatter:off
    @Shadow private int containerCounter;
    @Shadow public void nextContainerCounter() {}
    // @formatter:on

    @Unique
    private CraftEntity silkard$serverBukkitEntity;

    // CraftBukkit start
    public CraftPlayer.TransferCookieConnection transferCookieConnection;
    public String displayName;
    public Component listName;
    public int listOrder = 0;
    public org.bukkit.Location compassTarget;
    public int newExp = 0;
    public int newLevel = 0;
    public int newTotalExp = 0;
    public boolean keepLevel = false;
    public double maxHealthCache;
    public boolean joining = true;
    public boolean sentListPacket = false;
    public String kickLeaveMessage = null; // SPIGOT-3034: Forward leave message to PlayerQuitEvent
    // CraftBukkit end
    public PlayerSpawnChangeEvent.Cause respawnPositionCause = PlayerSpawnChangeEvent.Cause.UNKNOWN;
    public boolean initialized = false;

    public MixinServerPlayer(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Override
    public void setRespawnPosition(ServerPlayer.RespawnConfig respawnConfig, boolean showMessage, PlayerSpawnChangeEvent.Cause cause) {
        if (respawnConfig == null) {
            ((ServerPlayer) (Object) this).setRespawnPosition(null, showMessage);
            return;
        }

        PlayerSpawnChangeEvent event = new PlayerSpawnChangeEvent((org.bukkit.entity.Player) ((com.mohistmc.silkard.injected.world.entity.ContextEntity) (Object) this).getBukkitEntity(), null, false, cause);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            ((ServerPlayer) (Object) this).setRespawnPosition(respawnConfig, showMessage);
        }
    }

    @Override
    public int nextContainerCounterInt() {
        nextContainerCounter();
        return containerCounter;
    }

    @Override
    public void silkard$transferCookieConnection(CraftPlayer.TransferCookieConnection transferCookieConnection) {
        this.transferCookieConnection = transferCookieConnection;
    }

    @Override
    public CraftPlayer.TransferCookieConnection silkard$transferCookieConnection() {
        return this.transferCookieConnection;
    }

    @Override
    public boolean silkard$sentListPacket() {
        return this.sentListPacket;
    }

    @Override
    public void silkard$setSentListPacket(boolean sentListPacket) {
        this.sentListPacket = sentListPacket;
    }

    @Override
    public String silkard$kickLeaveMessage() {
        return this.kickLeaveMessage;
    }

    @Override
    public void silkard$setKickLeaveMessage(String kickLeaveMessage) {
        this.kickLeaveMessage = kickLeaveMessage;
    }

    // CraftBukkit - runtime NMS has no concrete getBukkitEntity() on ServerPlayer (only the ContextServerPlayer stub); provide the real lazily-created entity.
    public CraftPlayer getBukkitEntity() {
        if (this.silkard$serverBukkitEntity == null) {
            this.silkard$serverBukkitEntity = CraftEntity.getEntity(level().getCraftServer(), (Entity) (Object) this);
        }
        return (CraftPlayer) this.silkard$serverBukkitEntity;
    }

    @Override
    public String silkard$displayName() {

        return displayName;
    }

    @Override
    public void silkard$displayName(String displayName) {
        this.displayName = displayName;
    }

    // CraftBukkit start
    @Inject(method = "teleport", at = @At("HEAD"), cancellable = true)
    private void silkard$playerTeleportEvent(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
        if (this.isSleeping()) {
            cir.setReturnValue(null); // CraftBukkit - SPIGOT-3154: can't teleport while sleeping
            return;
        }

        ServerLevel newLevel = transition.newLevel();
        PositionMoveRotation absolutePosition = PositionMoveRotation.calculateAbsolute(PositionMoveRotation.of((ServerPlayer) (Object) this), PositionMoveRotation.of(transition), transition.relatives());
        Location enter = this.getBukkitEntity().getLocation();
        Location exit = CraftLocation.toBukkit(absolutePosition.position(), ((com.mohistmc.silkard.injected.world.level.ContextLevel) newLevel).getWorld(), absolutePosition.yRot(), absolutePosition.xRot());
        PlayerTeleportEvent tpEvent = new PlayerTeleportEvent(this.getBukkitEntity(), enter, exit, ((ContextTeleportTransition) (Object) transition).silkard$cause());
        Bukkit.getServer().getPluginManager().callEvent(tpEvent);

        if (tpEvent.isCancelled() || tpEvent.getTo() == null) {
            cir.setReturnValue(null);
        }
        // 插件修改目标位置（tpEvent.getTo() != exit 时重建 transition）待传送链后续补齐
    }
    // CraftBukkit end
}
