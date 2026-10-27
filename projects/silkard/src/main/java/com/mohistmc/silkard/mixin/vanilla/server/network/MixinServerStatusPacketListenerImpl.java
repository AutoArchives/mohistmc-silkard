package com.mohistmc.silkard.mixin.vanilla.server.network;

import com.mohistmc.silkard.bukkit.BukkitUtils;
import com.mohistmc.silkard.injected.network.ContextConnection;
import com.mohistmc.silkard.injected.server.ContextMinecraftServer;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;
import net.minecraft.server.players.NameAndId;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftIconCache;
import org.bukkit.entity.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @reason CraftBukkit - ServerListPingEvent for the status (server list) response.
 */
@Mixin(ServerStatusPacketListenerImpl.class)
public abstract class MixinServerStatusPacketListenerImpl {

    @Shadow @Final private Connection connection;
    @Shadow private boolean hasRequestedStatus;

    @Inject(method = "handleStatusRequest", at = @At("HEAD"), cancellable = true)
    private void silkard$handleStatusRequest(ServerboundStatusRequestPacket packet, CallbackInfo ci) {
        if (this.hasRequestedStatus) {
            return; // let vanilla disconnect with DISCONNECT_REASON
        }
        this.hasRequestedStatus = true;

        final MinecraftServer server = BukkitUtils.getServer();
        final CraftServer craftServer = ((ContextMinecraftServer) server).silkard$server();
        final Object[] players = server.getPlayerList().players.toArray();
        final String hostname = ((ContextConnection) this.connection).silkard$hostname();
        final java.net.InetAddress address = ((InetSocketAddress) this.connection.getRemoteAddress()).getAddress();

        class SilkardPing extends org.bukkit.event.server.ServerListPingEvent {
            CraftIconCache icon = craftServer.getServerIcon();

            SilkardPing() {
                super(hostname, address, server.getMotd(), server.getPlayerList().getMaxPlayers());
            }

            @Override
            public void setServerIcon(org.bukkit.util.CachedServerIcon icon) {
                if (!(icon instanceof CraftIconCache craftIcon)) {
                    throw new IllegalArgumentException(icon + " was not created by " + CraftServer.class);
                }
                this.icon = craftIcon;
            }

            @Override
            public Iterator<Player> iterator() {
                return new Iterator<Player>() {
                    int i;
                    int ret = Integer.MIN_VALUE;
                    ServerPlayer player;

                    @Override
                    public boolean hasNext() {
                        if (player != null) {
                            return true;
                        }
                        for (int length = players.length, i = this.i; i < length; i++) {
                            ServerPlayer player = (ServerPlayer) players[i];
                            if (player != null) {
                                this.i = i + 1;
                                this.player = player;
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public Player next() {
                        if (!hasNext()) {
                            throw new java.util.NoSuchElementException();
                        }
                        ServerPlayer player = this.player;
                        this.player = null;
                        this.ret = this.i - 1;
                        return player.getBukkitEntity();
                    }

                    @Override
                    public void remove() {
                        int i = this.ret;
                        if (i < 0 || players[i] == null) {
                            throw new IllegalStateException();
                        }
                        players[i] = null;
                    }
                };
            }
        }

        SilkardPing event = new SilkardPing();
        craftServer.getPluginManager().callEvent(event);

        List<NameAndId> profiles = new ArrayList<>(players.length);
        NameAndId anonymous = new NameAndId(new UUID(0L, 0L), "Anonymous Player");
        for (Object p : players) {
            if (p != null) {
                ServerPlayer sp = (ServerPlayer) p;
                profiles.add(sp.allowsListing() ? sp.nameAndId() : anonymous);
            }
        }

        ServerStatus.Players sample = new ServerStatus.Players(event.getMaxPlayers(), event.getNumPlayers(),
                server.hidesOnlinePlayers() ? Collections.emptyList() : profiles);
        ServerStatus ping = new ServerStatus(
                CraftChatMessage.fromString(event.getMotd(), true)[0],
                Optional.of(sample),
                Optional.of(new ServerStatus.Version(server.getServerModName() + " " + server.getServerVersion(), SharedConstants.getCurrentVersion().protocolVersion())),
                (event.icon.value != null) ? Optional.of(new ServerStatus.Favicon(event.icon.value)) : Optional.empty(),
                server.enforceSecureProfile()
        );
        this.connection.send(new ClientboundStatusResponsePacket(ping));
        ci.cancel();
    }
}
