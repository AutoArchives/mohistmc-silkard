package com.mohistmc.silkard.mixin.vanilla.server;

import com.mohistmc.silkard.bukkit.BukkitUtils;
import com.mohistmc.silkard.injected.server.ContextMinecraftServer;
import io.izzel.arclight.mixin.TransformAccess;
import joptsimple.OptionSet;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldLoader;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.jline.terminal.Terminal;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import com.mohistmc.silkard.injected.server.level.ContextServerLevel;
import com.mohistmc.silkard.injected.world.level.ContextLevel;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.worldupdate.UpgradeProgress;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.scores.ScoreboardSaveData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraft.world.Stopwatches;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner;
import org.bukkit.Bukkit;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/4/2 15:15
 */
@Mixin(MinecraftServer.class)
public class MixinMinecraftServer implements ContextMinecraftServer {

    private static final org.slf4j.Logger silkard$LOGGER = org.slf4j.LoggerFactory.getLogger(MixinMinecraftServer.class);

    // CraftBukkit start
    public WorldLoader.DataLoadContext worldLoader;
    public org.bukkit.craftbukkit.CraftServer server;
    public OptionSet options;
    public org.bukkit.command.ConsoleCommandSender console;
    public Terminal terminal;
    public static int currentTick = (int) (System.currentTimeMillis() / 50);
    public java.util.Queue<Runnable> processQueue = new java.util.concurrent.ConcurrentLinkedQueue<Runnable>();
    public int autosavePeriod;
    public Commands vanillaCommandDispatcher;
    private boolean forceTicks;
    // CraftBukkit end

    @Override
    public WorldLoader.DataLoadContext silkard_worldLoader() {
        return worldLoader;
    }

    @Override
    public CraftServer silkard$server() {
        return server;
    }

    @Override
    public Commands vanillaCommandDispatcher() {
        return vanillaCommandDispatcher;
    }

    @Override
    public OptionSet silkard_options() {
       return options;
    }

    @Override
    public ConsoleCommandSender silkard_console() {
        return console;
    }

    @Override
    public void silkard_console(ConsoleCommandSender console) {
        this.console = console;
    }

    @Override
    public java.util.Queue<Runnable> silkard_processQueue() {
        return processQueue;
    }

    @Override
    public int silkard_autosavePeriod() {
        return autosavePeriod;
    }

    @Override
    public void silkard_autosavePeriod(int autosavePeriod) {
        this.autosavePeriod = autosavePeriod;
    }

    @Unique
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static MinecraftServer getServer() {
        return BukkitUtils.getServer();
    }

    @Unique
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static RegistryAccess getDefaultRegistryAccess() {
        return BukkitUtils.getDefaultRegistryAccess();
    }
    // ==================== CraftBukkit world lifecycle ====================
    // CraftBukkit start
    @Mutable
    @Shadow
    @Final
    private Map<ResourceKey<Level>, ServerLevel> levels;

    @Shadow
    @Final
    protected net.minecraft.world.level.storage.WorldData worldData;

    @Shadow
    @Final
    private net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess storageSource;

    @Shadow
    @Final
    private net.minecraft.core.LayeredRegistryAccess<net.minecraft.server.RegistryLayer> registries;

    @Shadow
    @Final
    private java.util.concurrent.Executor executor;

    @Shadow
    @Final
    private net.minecraft.server.ServerScoreboard scoreboard;

    @Shadow
    @Nullable
    private CommandStorage commandStorage;

    @Shadow
    @Nullable
    private Stopwatches stopwatches;

    @Shadow
    @Final
    private LevelLoadListener levelLoadListener;

    @Shadow
    private long nextTickTimeNanos;

    @Shadow
    protected void forceDifficulty() {
        throw new AssertionError();
    }

    @Shadow
    public com.mojang.datafixers.DataFixer getFixerUpper() {
        throw new AssertionError();
    }

    @Shadow
    public net.minecraft.core.RegistryAccess.Frozen registryAccess() {
        throw new AssertionError();
    }

    @Shadow
    public String getServerModName() {
        throw new AssertionError();
    }

    @Shadow
    public net.minecraft.util.ModCheck getModdedStatus() {
        throw new AssertionError();
    }

    @Shadow
    public net.minecraft.server.players.PlayerList getPlayerList() {
        throw new AssertionError();
    }

    @Shadow
    public boolean isDemo() {
        throw new AssertionError();
    }

    @Shadow
    public int getAbsoluteMaxWorldSize() {
        throw new AssertionError();
    }

    @Shadow
    protected void waitUntilNextTick() {
        throw new AssertionError();
    }

    @Shadow
    private void setupDebugLevel(net.minecraft.world.level.storage.WorldData worldData) {
        throw new AssertionError();
    }

    @Shadow
    private static void setInitialSpawn(ServerLevel level, ServerLevelData levelData, boolean spawnBonusChest, boolean isDebug, LevelLoadListener levelLoadListener) {
        throw new AssertionError();
    }

    private void silkard$updateRespawnData() {
        this.updateMobSpawningFlags();
        this.updateEffectiveRespawnData();
    }

    @Shadow
    public void updateMobSpawningFlags() {
        throw new AssertionError();
    }

    @Shadow
    private void updateEffectiveRespawnData() {
        throw new AssertionError();
    }
    // CraftBukkit end


    // ==================== CraftBukkit world lifecycle ====================
    // CraftBukkit start
    @Override
    public void addLevel(ServerLevel level) {
        Map<ResourceKey<Level>, ServerLevel> oldLevels = this.levels;
        Map<ResourceKey<Level>, ServerLevel> newLevels = new java.util.LinkedHashMap<>(oldLevels);
        newLevels.put(level.dimension(), level);
        this.levels = java.util.Collections.unmodifiableMap(newLevels);
    }

    @Override
    public void removeLevel(ServerLevel level) {
        Map<ResourceKey<Level>, ServerLevel> oldLevels = this.levels;
        Map<ResourceKey<Level>, ServerLevel> newLevels = new java.util.LinkedHashMap<>(oldLevels);
        newLevels.remove(level.dimension());
        this.levels = java.util.Collections.unmodifiableMap(newLevels);
    }

    @Override
    public void prepareLevels(ServerLevel serverlevel) {
        net.minecraft.server.level.ChunkLoadCounter chunkLoadCounter = new net.minecraft.server.level.ChunkLoadCounter();
        chunkLoadCounter.track(serverlevel, () -> {
            net.minecraft.world.level.TicketStorage savedTickets = serverlevel.getDataStorage().get(net.minecraft.world.level.TicketStorage.TYPE);
            if (savedTickets != null) {
                savedTickets.activateAllDeactivatedTickets();
            }
        });
        do {
            this.levelLoadListener.update(net.minecraft.server.level.progress.LevelLoadListener.Stage.LOAD_INITIAL_CHUNKS, chunkLoadCounter.readyChunks(), chunkLoadCounter.totalChunks());
            this.nextTickTimeNanos = Util.getNanos() + 10000000L; // PREPARE_LEVELS_DEFAULT_DELAY_NANOS
            this.waitUntilNextTick();
        } while (chunkLoadCounter.pendingChunks() > 0);
        this.levelLoadListener.finish(net.minecraft.server.level.progress.LevelLoadListener.Stage.LOAD_INITIAL_CHUNKS);
        this.updateMobSpawningFlags();
        this.updateEffectiveRespawnData();
    }

    @Override
    public void initWorld(ServerLevel serverlevel, ServerLevelData serverleveldata, net.minecraft.world.level.storage.WorldData saveData, WorldOptions worldoptions) {
        boolean flag = saveData.isDebugWorld();
        // CraftBukkit start
        if (((com.mohistmc.silkard.injected.server.level.ContextServerLevel) serverlevel).silkard$generator() != null) {
            ((com.mohistmc.silkard.injected.world.level.ContextLevel) serverlevel).getWorld().getPopulators().addAll(((com.mohistmc.silkard.injected.server.level.ContextServerLevel) serverlevel).silkard$generator().getDefaultPopulators(((com.mohistmc.silkard.injected.world.level.ContextLevel) serverlevel).getWorld()));
        }
        this.silkard$initWorldBorder(serverlevel); // CraftBukkit - move up so that WorldBorder is set during WorldInitEvent
        this.server.getPluginManager().callEvent(new org.bukkit.event.world.WorldInitEvent(((com.mohistmc.silkard.injected.world.level.ContextLevel) serverlevel).getWorld())); // CraftBukkit - SPIGOT-5569: Call WorldInitEvent before any chunks are generated
        // CraftBukkit end
        if (!serverleveldata.isInitialized()) {
            try {
                setInitialSpawn(serverlevel, serverleveldata, worldoptions.generateBonusChest(), flag, this.levelLoadListener);
                serverleveldata.setInitialized(true);
                if (flag) {
                    this.setupDebugLevel(saveData);
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception initializing level");

                try {
                    serverlevel.fillReportDetails(crashreport);
                } catch (Throwable ignored) {
                }

                throw new ReportedException(crashreport);
            }

            serverleveldata.setInitialized(true);
        }

        net.minecraft.core.GlobalPos globalpos = new net.minecraft.core.GlobalPos(serverlevel.dimension(), serverlevel.getRespawnData().pos());
        this.levelLoadListener.updateFocus(globalpos.dimension(), net.minecraft.world.level.ChunkPos.containing(globalpos.pos()));
    }

    private void silkard$initWorldBorder(ServerLevel serverlevel) {
        serverlevel.getWorldBorder().setAbsoluteMaxSize(this.getAbsoluteMaxWorldSize());
        this.getPlayerList().addWorldborderListener(serverlevel);
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - per-world loading, plugins, chunk generators (loadWorld0)
     */
    @Overwrite
    protected void createLevels() {
        String s = this.storageSource.getLevelId(); // level name
        LevelStorageSource.LevelStorageAccess worldSession = this.storageSource;

        net.minecraft.core.RegistryAccess.Frozen registryaccess_frozen = this.registries.compositeAccess();
        net.minecraft.core.Registry<LevelStem> dimensions = registryaccess_frozen.lookupOrThrow(Registries.LEVEL_STEM);
        for (LevelStem worldDimension : dimensions) {
            ResourceKey<LevelStem> dimensionKey = dimensions.getResourceKey(worldDimension).get();

            ServerLevel world;
            int dimension = 0;

            if (dimensionKey == LevelStem.NETHER) {
                if (server.getAllowNether()) {
                    dimension = -1;
                } else {
                    continue;
                }
            } else if (dimensionKey == LevelStem.END) {
                if (server.getAllowEnd()) {
                    dimension = 1;
                } else {
                    continue;
                }
            } else if (dimensionKey != LevelStem.OVERWORLD) {
                dimension = -999;
            }

            String worldType = (dimension == -999) ? dimensionKey.identifier().getNamespace() + "_" + dimensionKey.identifier().getPath() : org.bukkit.World.Environment.getEnvironment(dimension).toString().toLowerCase(Locale.ROOT);
            String name = (dimensionKey == LevelStem.OVERWORLD) ? s : s + "_" + worldType;
            if (dimension != 0) {
                // CraftBukkit: per-dimension storage folders (pre-26.1 layout migration path simplified)
                Path newWorldFolder = java.nio.file.Paths.get(name);
                File newWorld = DimensionType.getStorageFolder(Registries.levelStemToLevel(dimensionKey), newWorldFolder).toFile();
                File oldWorld = DimensionType.getStorageFolder(Registries.levelStemToLevel(dimensionKey), new File(s).toPath()).toFile();
                File oldLevelDat = new File(new File(s), "level.dat");

                if (!newWorldFolder.toFile().isDirectory() && !newWorld.isDirectory() && oldWorld.isDirectory() && oldLevelDat.isFile()) {
                    silkard$LOGGER.info("---- Migration of old " + worldType + " folder required ----");
                    silkard$LOGGER.info("Unfortunately due to the way that Minecraft implemented multiworld support in 1.6, Bukkit requires that you move your " + worldType + " folder to a new location in order to operate correctly.");
                    silkard$LOGGER.info("We will move this folder for you, but it will mean that you need to move it back should you wish to stop using Bukkit in the future.");
                    silkard$LOGGER.info("Attempting to move " + oldWorld + " to " + newWorld + "...");

                    if (newWorld.exists()) {
                        silkard$LOGGER.warn("A file or folder already exists at " + newWorld + "!");
                        silkard$LOGGER.info("---- Migration of old " + worldType + " folder failed ----");
                    } else if (newWorld.getParentFile().mkdirs()) {
                        if (oldWorld.renameTo(newWorld)) {
                            silkard$LOGGER.info("Success! To restore " + worldType + " in the future, simply move " + newWorld + " to " + oldWorld);
                            // Migrate world data too.
                            try {
                                com.google.common.io.Files.copy(oldLevelDat, new File(new File(name), "level.dat"));
                                org.apache.commons.io.FileUtils.copyDirectory(new File(new File(s), "data"), new File(new File(name), "data"));
                            } catch (IOException exception) {
                                silkard$LOGGER.warn("Unable to migrate world data.");
                            }
                            silkard$LOGGER.info("---- Migration of old " + worldType + " folder complete ----");
                        } else {
                            silkard$LOGGER.warn("Could not move folder " + oldWorld + " to " + newWorld + "!");
                            silkard$LOGGER.info("---- Migration of old " + worldType + " folder failed ----");
                        }
                    } else {
                        silkard$LOGGER.warn("Could not create path for " + newWorld + "!");
                        silkard$LOGGER.info("---- Migration of old " + worldType + " folder failed ----");
                    }
                }

                try {
                    worldSession = LevelStorageSource.createDefault(server.getWorldContainer().toPath()).validateAndCreateAccess(name);
                } catch (IOException | ContentValidationException ex) {
                    throw new RuntimeException(ex);
                }
            }

            Dynamic<?> dynamic;
            if (worldSession.hasWorldData()) {
                Dynamic<?> dynamic1;
                try {
                    dynamic1 = worldSession.getUnfixedDataTagWithFallback();
                } catch (net.minecraft.nbt.NbtException | net.minecraft.nbt.ReportedNbtException | IOException ioexception) {
                    silkard$LOGGER.error("Failed to load world data. World files may be corrupted. Shutting down.", ioexception);
                    return;
                }

                LevelSummary levelsummary = worldSession.fixAndGetSummaryFromTag(dynamic1);
                if (levelsummary.requiresManualConversion()) {
                    silkard$LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
                    return;
                }
                if (!levelsummary.isCompatible()) {
                    silkard$LOGGER.info("This world was created by an incompatible version.");
                    return;
                }

                dynamic = DataFixers.getFileFixer().fix(worldSession, dynamic1, new UpgradeProgress());
            } else {
                dynamic = null;
            }

            org.bukkit.generator.ChunkGenerator gen = this.server.getGenerator(name);
            org.bukkit.generator.BiomeProvider biomeProvider = this.server.getBiomeProvider(name);

            LevelDataAndDimensions.WorldDataAndGenSettings dataAndSettings;
            WorldLoader.DataLoadContext dataloadcontext = this.worldLoader;
            net.minecraft.core.Registry<LevelStem> registry = dataloadcontext.datapackDimensions().lookupOrThrow(Registries.LEVEL_STEM);
            if (dynamic != null) {
                LevelDataAndDimensions leveldataanddimensions = LevelStorageSource.getLevelDataAndDimensions(worldSession, dynamic, dataloadcontext.dataConfiguration(), registry, dataloadcontext.datapackWorldRegistries());
                dataAndSettings = leveldataanddimensions.worldDataAndGenSettings();
            } else {
                LevelSettings levelsettings;
                WorldOptions worldoptions;
                WorldDimensions worlddimensions;

                if (this.isDemo()) {
                    levelsettings = MinecraftServer.DEMO_SETTINGS;
                    worldoptions = WorldOptions.DEMO_OPTIONS;
                    worlddimensions = WorldPresets.createNormalWorldDimensions(dataloadcontext.datapackWorldRegistries());
                } else {
                    DedicatedServerProperties dedicatedserverproperties = ((DedicatedServer) (Object) this).getProperties();
                    levelsettings = new LevelSettings(dedicatedserverproperties.levelName, dedicatedserverproperties.gameMode.get(), new LevelSettings.DifficultySettings(dedicatedserverproperties.difficulty.get(), dedicatedserverproperties.hardcore, false), false, dataloadcontext.dataConfiguration());
                    worldoptions = options.has("bonusChest") ? dedicatedserverproperties.worldOptions.withBonusChest(true) : dedicatedserverproperties.worldOptions;
                    worlddimensions = dedicatedserverproperties.createDimensions(dataloadcontext.datapackWorldRegistries());
                }

                WorldDimensions.Complete worlddimensions_complete = worlddimensions.bake(registry);
                Lifecycle lifecycle = worlddimensions_complete.lifecycle().add(dataloadcontext.datapackWorldRegistries().allRegistriesLifecycle());

                PrimaryLevelData primaryleveldata = new PrimaryLevelData(levelsettings, worlddimensions_complete.specialWorldProperty(), lifecycle);
                dataAndSettings = new LevelDataAndDimensions.WorldDataAndGenSettings(primaryleveldata, new WorldGenSettings(worldoptions, worlddimensions));
            }
            PrimaryLevelData worlddata = (PrimaryLevelData) dataAndSettings.data();
            // CraftBukkit: worlddata.checkName(name); (26.3 vanilla removed checkName — level name carried by LevelStorageAccess)
            // CraftBukkit: forceUpgrade handled by net.minecraft.server.Main (deferred)

            SavedDataStorage dimensiondatastorage = new SavedDataStorage(worldSession.getLevelPath(net.minecraft.world.level.storage.LevelResource.DATA), this.getFixerUpper(), this.registryAccess());

            ServerLevelData serverleveldata = worlddata.overworldData();
            boolean flag = worlddata.isDebugWorld();
            WorldOptions worldoptions = dataAndSettings.genSettings().options();
            long i = worldoptions.seed();
            long j = BiomeManager.obfuscateSeed(i);
            List<net.minecraft.world.level.CustomSpawner> list = com.google.common.collect.ImmutableList.of(new net.minecraft.world.level.levelgen.PhantomSpawner(), new net.minecraft.world.level.levelgen.PatrolSpawner(), new net.minecraft.world.entity.npc.CatSpawner(), new net.minecraft.world.entity.ai.village.VillageSiege(), new net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner(dimensiondatastorage));
            LevelStem levelstem = dimensions.getValue(dimensionKey);

            org.bukkit.generator.WorldInfo worldInfo = new org.bukkit.craftbukkit.generator.CraftWorldInfo(serverleveldata, worldoptions, worldSession, org.bukkit.World.Environment.getEnvironment(dimension), levelstem.type().value());
            if (biomeProvider == null && gen != null) {
                biomeProvider = gen.getDefaultBiomeProvider(worldInfo);
            }

            ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, dimensionKey.identifier());

            if (dimensionKey == LevelStem.OVERWORLD) {
                this.worldData = worlddata;
                this.worldData.setGameType(((DedicatedServer) (Object) this).getProperties().gameMode.get()); // From DedicatedServer.init

                world = new ServerLevel((MinecraftServer) (Object) this, this.executor, worldSession, worlddata, worldKey, levelstem, flag, j, list, true);
                this.scoreboard.load(((ScoreboardSaveData) dimensiondatastorage.computeIfAbsent(ScoreboardSaveData.TYPE)).getData());
                this.server.scoreboardManager = new org.bukkit.craftbukkit.scoreboard.CraftScoreboardManager((MinecraftServer) (Object) this, world.getScoreboard());
                this.commandStorage = new CommandStorage(dimensiondatastorage);
                this.stopwatches = (Stopwatches) dimensiondatastorage.computeIfAbsent(Stopwatches.TYPE);
            } else {
                world = new ServerLevel((MinecraftServer) (Object) this, this.executor, worldSession, worlddata, worldKey, levelstem, flag, j, com.google.common.collect.ImmutableList.of(), true);
            }

            ((ContextServerLevel) world).silkard$setEnvironment(org.bukkit.World.Environment.getEnvironment(dimension));
            ((ContextServerLevel) world).silkard$setGenerator(gen, biomeProvider);
            worlddata.setModdedInfo(this.getServerModName(), this.getModdedStatus().shouldReportAsModified());
            this.initWorld(world, serverleveldata, worlddata, worldoptions);

            this.addLevel(world);
            this.getPlayerList().addWorldborderListener(world);
        }
        this.forceDifficulty();
        for (ServerLevel serverlevel : java.util.List.copyOf(this.levels.values())) {
            this.prepareLevels(serverlevel);
            ((com.mohistmc.silkard.injected.server.level.ContextServerLevel) serverlevel).silkard_tickEntityManager(); // SPIGOT-6526
            this.server.getPluginManager().callEvent(new WorldLoadEvent(((com.mohistmc.silkard.injected.world.level.ContextLevel) serverlevel).getWorld()));
        }

        this.server.enablePlugins(org.bukkit.plugin.PluginLoadOrder.POSTWORLD);
        this.server.getPluginManager().callEvent(new ServerLoadEvent(ServerLoadEvent.LoadType.STARTUP));
    }
    // CraftBukkit end
}
