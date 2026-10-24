package com.mohistmc.silkard.mixin.vanilla.server.players;

import com.mohistmc.silkard.injected.server.level.ContextServerPlayer;
import com.mohistmc.silkard.injected.server.players.ContextPlayerList;
import com.mohistmc.silkard.injected.world.inventory.ContextAbstractContainerMenu;
import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.util.FileUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/4/2 19:11
 */
@Mixin(PlayerList.class)
public abstract class MixinPlayerList implements ContextPlayerList {

    // @formatter:off
    @Shadow public @Nullable abstract ServerPlayer getPlayer(UUID uuid);
    @Shadow @Final private MinecraftServer server;
    @Shadow @Final public List<ServerPlayer> players;
    @Shadow @Final private Map<UUID, ServerPlayer> playersByUUID;
    @Shadow @Final private Map<UUID, ServerStatsCounter> stats;
    @Shadow @Final private UserBanList bans;
    @Shadow @Final private IpBanList ipBans;
    // @formatter:on

    // CraftBukkit start - CraftServer construction (PlayerList.patch ctor hunk)
    @Inject(method = "<init>", at = @At("TAIL"))
    private void silkard$bukkitServerInit(MinecraftServer server, net.minecraft.core.LayeredRegistryAccess<net.minecraft.server.RegistryLayer> registries, net.minecraft.world.level.storage.PlayerDataStorage playerIo, net.minecraft.server.notifications.NotificationService notificationService, CallbackInfo ci) {
        if (((com.mohistmc.silkard.injected.server.ContextMinecraftServer) this.server).silkard$server() == null) {
            ((com.mohistmc.silkard.injected.server.ContextMinecraftServer) this.server).silkard$setServer(new org.bukkit.craftbukkit.CraftServer((net.minecraft.server.dedicated.DedicatedServer) (Object) this.server, (PlayerList) (Object) this));
            ((com.mohistmc.silkard.injected.server.ContextMinecraftServer) this.server).silkard_console(org.bukkit.craftbukkit.command.ColouredConsoleSender.getInstance());
            org.bukkit.Bukkit.setServer(((com.mohistmc.silkard.injected.server.ContextMinecraftServer) this.server).silkard$server());
        }
    }
    // CraftBukkit end

    @Override
    public ServerStatsCounter getPlayerStats(NameAndId gameprofile) {
        ServerPlayer entityhuman = this.getPlayer(gameprofile.id());
        ServerStatsCounter serverstatscounter = entityhuman == null ? null : (ServerStatsCounter) entityhuman.getStats();
        if (serverstatscounter == null) {
            Path path = this.locateStatsFile(gameprofile);

            return new ServerStatsCounter(this.server, path);
        }
        return serverstatscounter;
    }

    @Unique
    private Path locateStatsFile(NameAndId gameProfile) {
        // CraftBukkit end
        Path path = this.server.getWorldPath(LevelResource.PLAYER_STATS_DIR);
        Path path1 = path.resolve(String.valueOf(gameProfile.id()) + ".json");

        if (Files.exists(path1, new LinkOption[0])) {
            return path1;
        } else {
            String s = gameProfile.name() + ".json";

            if (FileUtil.isValidPathSegment(s)) {
                Path path2 = path.resolve(s);

                if (Files.isRegularFile(path2, new LinkOption[0])) {
                    try {
                        return Files.move(path2, path1);
                    } catch (IOException ioexception) {
                        PlayerList.LOGGER.warn("Failed to copy file {} to {}", s, path1);
                        return path2;
                    }
                }
            }

            return path1;
        }
    }

    // CraftBukkit start - CraftServer accessor for PlayerList-side event dispatch
    @Override
    public CraftServer getCraftServer() {
        return ((com.mohistmc.silkard.injected.server.ContextMinecraftServer) this.server).silkard$server();
    }
    // CraftBukkit end

    // CraftBukkit start - PlayerLoginEvent: ban/whitelist/ipban/full checks become event disallows
    @Override
    public ServerPlayer canPlayerLogin(ServerLoginPacketListenerImpl serverloginpacketlistenerimpl, GameProfile gameprofile) {
        NameAndId nameAndId = new NameAndId(gameprofile);
        UUID uuid = gameprofile.id();
        Set<ServerPlayer> set = Sets.newIdentityHashSet();

        for (ServerPlayer serverplayer : this.players) {
            if (serverplayer.getUUID().equals(uuid)) {
                set.add(serverplayer);
            }
        }

        for (ServerPlayer serverplayer2 : set) {
            ((PlayerList) (Object) this).save(serverplayer2);
            serverplayer2.connection.disconnect(PlayerList.DUPLICATE_LOGIN_DISCONNECT_MESSAGE);
        }

        Connection connection = ((com.mohistmc.silkard.injected.server.network.ContextServerLoginPacketListenerImpl) serverloginpacketlistenerimpl).silkard$connection();
        SocketAddress address = connection.getRemoteAddress();

        ServerPlayer entity = new ServerPlayer(this.server, this.server.getLevel(Level.OVERWORLD), gameprofile, ClientInformation.createDefault());
        org.bukkit.entity.Player player = entity.getBukkitEntity();
        PlayerLoginEvent event = new PlayerLoginEvent(player, ((com.mohistmc.silkard.injected.network.ContextConnection) connection).silkard$hostname(), ((java.net.InetSocketAddress) address).getAddress());

        if (this.bans.isBanned(nameAndId)) {
            UserBanListEntry userbanlistentry = (UserBanListEntry) this.bans.get(nameAndId);
            MutableComponent mutablecomponent = Component.translatable("multiplayer.disconnect.banned.reason", userbanlistentry.getReasonMessage());
            if (userbanlistentry.getExpires() != null) {
                mutablecomponent.append(Component.translatable("multiplayer.disconnect.banned.expiration", PlayerList.BAN_DATE_FORMAT.format(userbanlistentry.getExpires())));
            }
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, CraftChatMessage.fromComponent(mutablecomponent));
        } else if (!((PlayerList) (Object) this).isWhiteListed(nameAndId)) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, CraftChatMessage.fromComponent(Component.translatable("multiplayer.disconnect.not_whitelisted")));
        } else if (this.ipBans.isBanned(address)) {
            IpBanListEntry ipbanlistentry = this.ipBans.get(address);
            MutableComponent mutablecomponent1 = Component.translatable("multiplayer.disconnect.banned_ip.reason", ipbanlistentry.getReasonMessage());
            if (ipbanlistentry.getExpires() != null) {
                mutablecomponent1.append(Component.translatable("multiplayer.disconnect.banned_ip.expiration", PlayerList.BAN_DATE_FORMAT.format(ipbanlistentry.getExpires())));
            }
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, CraftChatMessage.fromComponent(mutablecomponent1));
        } else if (this.players.size() >= ((PlayerList) (Object) this).getMaxPlayers() && !((PlayerList) (Object) this).canBypassPlayerLimit(nameAndId)) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, "The server is full");
        }

        this.getCraftServer().getPluginManager().callEvent(event);
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            ((com.mohistmc.silkard.injected.server.network.ContextServerLoginPacketListenerImpl) serverloginpacketlistenerimpl).silkard$setKickMessage(Component.literal(event.getKickMessage()));
            return null;
        }
        return entity;
    }

    @Override
    public boolean disconnectAllPlayersWithProfile(UUID playerId, ServerPlayer player) {
        return player == null;
    }
    // CraftBukkit end

    // CraftBukkit start - split reloadResources so recipes can be re-sent after addRecipe
    @Shadow
    @Final
    private Map<UUID, PlayerAdvancements> advancements;

    @Shadow
    @Final
    private LayeredRegistryAccess<RegistryLayer> registries;

    @Shadow
    public abstract void broadcastAll(Packet<?> packet);

    /**
     * @author Mgazul
     * @reason CraftBukkit - extract recipe resending into reloadRecipes
     */
    @Overwrite
    public void reloadResources() {
        for (PlayerAdvancements advancements : this.advancements.values()) {
            advancements.reload(this.server.getAdvancements());
        }

        this.broadcastAll(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(this.registries)));
        this.reloadRecipes();
    }

    @Override
    public void reloadRecipes() {
        RecipeManager recipemanager = this.server.getRecipeManager();
        ClientboundUpdateRecipesPacket clientboundupdaterecipespacket = new ClientboundUpdateRecipesPacket(recipemanager.getSynchronizedItemProperties(), recipemanager.getSynchronizedStonecutterRecipes());

        for (ServerPlayer player : this.players) {
            player.connection.send(clientboundupdaterecipespacket);
            player.getRecipeBook().sendInitialRecipeBook(player);
        }
    }
    // CraftBukkit end

    /**
     * @author Mgazul
     * @reason CraftBukkit - PlayerJoinEvent with plugin-controlled join message, visibility-aware player list
     */
    @Overwrite
    public void placeNewPlayer(Connection connection, ServerPlayer player, CommonListenerCookie cookie) {
        NameAndId nameAndId = player.nameAndId();
        net.minecraft.server.players.UserNameToIdResolver usernametoidresolver = this.server.services().nameToIdCache();
        String s = usernametoidresolver.get(nameAndId.id()).map(NameAndId::name).orElse(nameAndId.name());
        // CraftBukkit start - better rename detection
        String lastKnownName = player.getBukkitEntity().getLastKnownName();
        if (lastKnownName != null) {
            s = lastKnownName;
        }
        // CraftBukkit end
        usernametoidresolver.add(nameAndId);
        ServerLevel serverlevel = player.level();
        String s1 = connection.getLoggableAddress(this.server.logIPs());

        // CraftBukkit - moved message to after join
        LevelData leveldata = serverlevel.getLevelData();
        ServerGamePacketListenerImpl servergamepacketlistenerimpl = new ServerGamePacketListenerImpl(this.server, connection, player, cookie);

        connection.setupInboundProtocol(GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(this.server.registryAccess()), servergamepacketlistenerimpl), servergamepacketlistenerimpl);
        servergamepacketlistenerimpl.suspendFlushing();
        GameRules gamerules = serverlevel.getGameRules();

        boolean flag = gamerules.get(GameRules.IMMEDIATE_RESPAWN);
        boolean flag1 = gamerules.get(GameRules.REDUCED_DEBUG_INFO);
        boolean flag2 = gamerules.get(GameRules.LIMITED_CRAFTING);

        servergamepacketlistenerimpl.send(new ClientboundLoginPacket(player.getId(), leveldata.isHardcore(), this.server.levelKeys(), ((PlayerList) (Object) this).getMaxPlayers(), ((PlayerList) (Object) this).getViewDistance(), ((PlayerList) (Object) this).getSimulationDistance(), flag1, !flag, flag2, player.createCommonSpawnInfo(serverlevel), this.server.usesAuthentication(), this.server.enforceSecureProfile()));
        player.getBukkitEntity().sendSupportedChannels(); // CraftBukkit
        servergamepacketlistenerimpl.send(new ClientboundChangeDifficultyPacket(leveldata.getDifficulty(), leveldata.isDifficultyLocked()));
        servergamepacketlistenerimpl.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
        servergamepacketlistenerimpl.send(new ClientboundSetHeldSlotPacket(player.getInventory().getSelectedSlot()));
        RecipeManager recipemanager = this.server.getRecipeManager();

        servergamepacketlistenerimpl.send(new ClientboundUpdateRecipesPacket(recipemanager.getSynchronizedItemProperties(), recipemanager.getSynchronizedStonecutterRecipes()));
        ((PlayerList) (Object) this).sendPlayerPermissionLevel(player);
        player.getStats().markAllDirty();
        player.getRecipeBook().sendInitialRecipeBook(player);
        ((PlayerList) (Object) this).updateEntireScoreboard(serverlevel.getScoreboard(), player);
        this.server.invalidateStatus();
        MutableComponent mutablecomponent;

        if (player.getGameProfile().name().equalsIgnoreCase(s)) {
            mutablecomponent = Component.translatable("multiplayer.player.joined", player.getDisplayName());
        } else {
            mutablecomponent = Component.translatable("multiplayer.player.joined.renamed", player.getDisplayName(), s);
        }

        // CraftBukkit start
        mutablecomponent.withStyle(net.minecraft.ChatFormatting.YELLOW);
        String joinMessage = CraftChatMessage.fromComponent(mutablecomponent);

        servergamepacketlistenerimpl.teleport(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        ServerStatus serverstatus = this.server.getStatus();

        if (serverstatus != null && !cookie.transferred()) {
            player.sendServerStatus(serverstatus);
        }

        CraftPlayer bukkitPlayer = player.getBukkitEntity();

        // Ensure that player inventory is populated with its viewer
        ((ContextAbstractContainerMenu) player.containerMenu).transferTo(player.containerMenu, bukkitPlayer);

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(bukkitPlayer, joinMessage);
        this.getCraftServer().getPluginManager().callEvent(playerJoinEvent);

        if (!player.connection.isAcceptingMessages()) {
            return;
        }

        joinMessage = playerJoinEvent.getJoinMessage();

        if (joinMessage != null && joinMessage.length() > 0) {
            for (Component line : CraftChatMessage.fromString(joinMessage)) {
                ((PlayerList) (Object) this).broadcastSystemMessage(line, false);
            }
        }
        // CraftBukkit end

        // CraftBukkit start - sendAll above replaced with this loop
        ClientboundPlayerInfoUpdatePacket packet = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(player));

        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayer serverplayer1 = this.players.get(i);

            if (serverplayer1.getBukkitEntity().canSee(bukkitPlayer)) {
                serverplayer1.connection.send(packet);
            }

            if (!bukkitPlayer.canSee(serverplayer1.getBukkitEntity())) {
                continue;
            }

            player.connection.send(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(serverplayer1)));
        }
        ((ContextServerPlayer) player).silkard$setSentListPacket(true);
        // CraftBukkit end

        ((PlayerList) (Object) this).sendLevelInfo(player, player.level()); // CraftBukkit - SPIGOT-8149 per-world time sync

        // CraftBukkit start - only add if the player wasn't moved in the event
        if (player.level() == serverlevel && !serverlevel.players().contains(player)) {
            serverlevel.addNewPlayer(player);
            this.server.getCustomBossEvents().onPlayerConnect(player);
        }

        serverlevel = player.level(); // CraftBukkit - update in case join event changed it
        // CraftBukkit end
        ((PlayerList) (Object) this).sendActivePlayerEffects(player);
        player.sendPostEffects();
        player.initInventoryMenu();
        this.server.notificationManager().playerJoined(player);
        servergamepacketlistenerimpl.resumeFlushing();
        // CraftBukkit - moved from above, added world
        PlayerList.LOGGER.info("{}[{}] logged in with entity id {} at ([{}]{}, {}, {})", player.getPlainTextName(), s1, player.getId(), ((ServerLevelData) serverlevel.getLevelData()).getLevelName(), player.getX(), player.getY(), player.getZ());
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - PlayerQuitEvent before the final save, visibility-aware player list removal
     */
    @Overwrite
    public void remove(ServerPlayer player) {
        ServerLevel serverlevel = player.level();

        player.awardStat(Stats.LEAVE_GAME);

        // CraftBukkit start - quitting must be before the final save, plugins may modify data (SPIGOT-5799, SPIGOT-6145)
        if (player.containerMenu != player.inventoryMenu) {
            player.closeContainer();
        }

        String leaveMessage = ((ContextServerPlayer) player).silkard$kickLeaveMessage();
        PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(player.getBukkitEntity(), leaveMessage != null ? leaveMessage : "\u00A7e" + player.getScoreboardName() + " left the game");
        this.getCraftServer().getPluginManager().callEvent(playerQuitEvent);
        player.getBukkitEntity().disconnect(playerQuitEvent.getQuitMessage());

        player.doTick(); // SPIGOT-924
        // CraftBukkit end

        ((PlayerList) (Object) this).save(player);
        if (player.isPassenger()) {
            Entity entity = player.getRootVehicle();

            if (entity.hasExactlyOnePlayerPassenger()) {
                PlayerList.LOGGER.debug("Removing player mount");
                player.stopRiding();
                entity.getPassengersAndSelf().forEach((entity1) -> {
                    ((ContextEntity) entity1).setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER, EntityRemoveEvent.Cause.PLAYER_QUIT);
                });
            }
        }

        player.unRide();

        for (ThrownEnderpearl thrownenderpearl : player.getEnderPearls()) {
            ((ContextEntity) thrownenderpearl).setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER, EntityRemoveEvent.Cause.PLAYER_QUIT);
        }

        serverlevel.removePlayerImmediately(player, Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        player.getAdvancements().clearTriggers();
        this.players.remove(player);
        this.server.getCustomBossEvents().onPlayerDisconnect(player);
        UUID uuid = player.getUUID();

        if (this.playersByUUID.get(uuid) == player) {
            this.playersByUUID.remove(uuid);
            this.stats.remove(uuid);
            this.advancements.remove(uuid);
            this.server.notificationManager().playerLeft(player);
        }

        // CraftBukkit start - only send to players that can see the leaving player
        ClientboundPlayerInfoRemovePacket packet = new ClientboundPlayerInfoRemovePacket(List.of(player.getUUID()));
        for (int i = 0; i < this.players.size(); i++) {
            ServerPlayer serverplayer2 = this.players.get(i);

            if (serverplayer2.getBukkitEntity().canSee(player.getBukkitEntity())) {
                serverplayer2.connection.send(packet);
            } else {
                serverplayer2.getBukkitEntity().onEntityRemove(player);
            }
        }
        this.getCraftServer().getScoreboardManager().removePlayer(player.getBukkitEntity());
        // CraftBukkit end
    }
}
