package com.mohistmc.silkard.mixin.vanilla.server.level;

import com.mohistmc.silkard.injected.server.level.ContextChunkMap;
import com.mohistmc.silkard.injected.server.level.ContextServerChunkCache;
import com.mohistmc.silkard.injected.world.level.ContextSpawnState;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.storage.LevelData;
import org.bukkit.craftbukkit.util.CraftSpawnCategory;
import org.bukkit.entity.SpawnCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(ServerChunkCache.class)
public abstract class MixinServerChunkCache implements ContextServerChunkCache {

    // CraftBukkit start - properly implement isChunkLoaded
    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    private net.minecraft.server.level.ChunkMap chunkMap;

    @Shadow
    @Final
    private ServerLevel level;

    @Shadow
    @Final
    private net.minecraft.server.level.ThreadedLevelLightEngine lightEngine;

    @Shadow
    @Final
    private net.minecraft.world.level.storage.SavedDataStorage savedDataStorage;

    @Shadow
    @Final
    private net.minecraft.world.level.TicketStorage ticketStorage;

    @Shadow
    public abstract void save(boolean save);

    @Shadow
    private void clearCache() {
        throw new AssertionError();
    }

    @Shadow
    boolean runDistanceManagerUpdates() {
        throw new AssertionError();
    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        net.minecraft.server.level.ChunkHolder chunk = this.chunkMap.getUpdatingChunkIfPresent(net.minecraft.world.level.ChunkPos.pack(chunkX, chunkZ));
        if (chunk == null) {
            return false;
        }
        return chunk.getChunkIfPresent(net.minecraft.world.level.chunk.status.ChunkStatus.FULL) != null; // CraftBukkit - 26.3 GenerationChunkHolder
    }
    // CraftBukkit end

    // CraftBukkit - run chunk callbacks in pollTask finally
    @org.spongepowered.asm.mixin.injection.Inject(method = "pollTask", at = @org.spongepowered.asm.mixin.injection.At("RETURN"))
    private void silkard$runChunkCallbacks(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        ((com.mohistmc.silkard.injected.server.level.ContextChunkMap) this.chunkMap).silkard$callbackExecutor().run();
    }

    // CraftBukkit start - close without saving
    @Override
    public void close(boolean save) throws IOException {
        if (save) {
            this.save(true);
        }
        this.savedDataStorage.close();
        this.lightEngine.close();
        this.chunkMap.close();
    }
    // CraftBukkit end

    // CraftBukkit start - purge unloading chunks
    @Override
    public void purgeUnload() {
        ProfilerFiller profilerfiller = Profiler.get();

        profilerfiller.push("purge");
        this.ticketStorage.purgeStaleTickets(this.chunkMap);
        this.runDistanceManagerUpdates();
        profilerfiller.popPush("unload");
        ((ContextChunkMap) (Object) this.chunkMap).silkard$tick(() -> true);
        profilerfiller.pop();
        this.clearCache();    }
    // CraftBukkit end

    // CraftBukkit start - per-world spawn limits
    @Redirect(method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;getFilteredSpawningCategories(Lnet/minecraft/world/level/NaturalSpawner$SpawnState;ZZ)Ljava/util/List;"))
    private List<MobCategory> silkard$filteredSpawningCategories(NaturalSpawner.SpawnState state, boolean spawnEnemies, boolean spawnPersistent) {
        ServerLevel serverlevel = this.level;
        LevelData leveldata = serverlevel.getLevelData();
        List<MobCategory> list = new ArrayList<>();

        for (MobCategory mobcategory : MobCategory.values()) {
            if (mobcategory == MobCategory.MISC) {
                continue;
            }

            boolean spawnThisTick = true;
            int limit = mobcategory.getMaxInstancesPerChunk();
            SpawnCategory spawnCategory = CraftSpawnCategory.toBukkit(mobcategory);
            if (CraftSpawnCategory.isValidForLimits(spawnCategory)) {
                long ticks = serverlevel.silkard$ticksPerSpawnCategory().getLong(spawnCategory);
                spawnThisTick = ticks != 0L && leveldata.getGameTime() % ticks == 0L;
                limit = serverlevel.getWorld().getSpawnLimit(spawnCategory);
            }

            if (!spawnThisTick || limit == 0) {
                continue;
            }

            if ((spawnEnemies || mobcategory.isFriendly()) && (spawnPersistent || !mobcategory.isPersistent()) && ((ContextSpawnState) state).canSpawnForCategory(mobcategory, limit)) {
                list.add(mobcategory);
            }
        }

        return list;
    }
    // CraftBukkit end
}
