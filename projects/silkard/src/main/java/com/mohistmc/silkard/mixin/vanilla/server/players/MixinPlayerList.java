package com.mohistmc.silkard.mixin.vanilla.server.players;

import com.mohistmc.silkard.injected.server.players.ContextPlayerList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.util.FileUtil;
import net.minecraft.world.level.storage.LevelResource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import java.util.List;
import java.util.Map;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.RegistryLayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/4/2 19:11
 */
@Mixin(PlayerList.class)
public abstract class MixinPlayerList implements ContextPlayerList {

    // @formatter:off
    @Shadow public @Nullable abstract ServerPlayer getPlayer(UUID uuid);
    @Shadow @Final private MinecraftServer server;
    // @formatter:on

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

    // CraftBukkit start - split reloadResources so recipes can be re-sent after addRecipe
    @Shadow
    @Final
    private List<ServerPlayer> players;

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
}
