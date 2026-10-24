package com.mohistmc.silkard.mixin.vanilla.server.level;

import com.mohistmc.silkard.injected.server.level.ContextServerLevel;
import java.util.UUID;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.saveddata.WeatherData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.timers.TimerQueue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/4/2 18:11
 */
@Mixin(ServerLevel.class)
public abstract class MixinServerLevel implements ContextServerLevel {

    // @formatter:off
    @Shadow @Final private ServerChunkCache chunkSource;
    @Shadow @Final public ServerLevelData serverLevelData;
    @Shadow public abstract WeatherData getWeatherData();
    // @formatter:on

    // CraftBukkit start
    public LevelStorageSource.LevelStorageAccess storageSource;
    public UUID uuid;
    // Moved from MinecraftServer start
    private SavedDataStorage savedDataStorage;
    private GameRules gameRules;
    private WorldGenSettings worldGenSettings;
    private CustomBossEvents customBossEvents;
    private RandomSequences randomSequences;
    private WeatherData weatherData;
    private TimerQueue<MinecraftServer> scheduledEvents;
    private ServerClockManager clockManager;

    public WorldGenSettings getWorldGenSettings() {
        return this.worldGenSettings;
    }

    public CustomBossEvents getCustomBossEvents() {
        return this.customBossEvents;
    }

    public RandomSource getRandomSequence(Identifier key) {
        return this.randomSequences.get(key, this.worldGenSettings.options().seed());
    }

    public RandomSequences getRandomSequences() {
        return this.randomSequences;
    }

    public void setWeatherParameters(int clearTime, int rainTime, boolean raining, boolean thundering) {
        WeatherData weatherdata = this.getWeatherData();

        weatherdata.setClearWeatherTime(clearTime);
        weatherdata.setRainTime(rainTime);
        weatherdata.setThunderTime(rainTime);
        weatherdata.setRaining(raining);
        weatherdata.setThundering(thundering);
    }

    public TimerQueue<MinecraftServer> getScheduledEvents() {
        return this.scheduledEvents;
    }
    // Moved from MinecraftServer end

    public LevelChunk getChunkIfLoaded(int x, int z) {
        return this.chunkSource.getChunk(x, z, false);
    }

    @Override
    public ResourceKey<LevelStem> getTypeKey() {
        return storageSource.silkard$getTypeKey();
    }

    @Override
    public LevelStorageSource.LevelStorageAccess silkard_storageSource() {
       return storageSource;
    }

    // CraftBukkit start
    public org.bukkit.generator.ChunkGenerator generator;
    public org.bukkit.generator.BiomeProvider biomeProvider;
    public org.bukkit.World.Environment environment;

    @Override
    public org.bukkit.generator.ChunkGenerator silkard$generator() {
        return this.generator;
    }

    @Override
    public org.bukkit.generator.BiomeProvider silkard$biomeProvider() {
        return this.biomeProvider;
    }

    @Override
    public void silkard$setGenerator(org.bukkit.generator.ChunkGenerator gen, org.bukkit.generator.BiomeProvider provider) {
        this.generator = gen;
        this.biomeProvider = provider;
    }

    @Override
    public org.bukkit.World.Environment silkard$environment() {
        return this.environment == null ? org.bukkit.World.Environment.NORMAL : this.environment;
    }

    @Override
    public void silkard$setEnvironment(org.bukkit.World.Environment env) {
        this.environment = env;
    }
    // CraftBukkit end

    @Override
    public void silkard_tickEntityManager() {
        this.entityManager.tick(); // SPIGOT-6526
    }
}
