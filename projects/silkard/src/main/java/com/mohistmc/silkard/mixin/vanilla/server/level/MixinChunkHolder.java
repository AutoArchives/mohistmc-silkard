package com.mohistmc.silkard.mixin.vanilla.server.level;

import com.mohistmc.silkard.injected.server.level.ContextChunkHolder;
import com.mohistmc.silkard.injected.server.level.ContextChunkMap;
import com.mohistmc.silkard.injected.world.level.chunk.ContextLevelChunk;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.concurrent.Executor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ChunkHolder.class)
public abstract class MixinChunkHolder implements ContextChunkHolder {

    @Shadow
    private int oldTicketLevel;

    @Shadow
    private int ticketLevel;

    @Shadow
    @Final
    private ShortSet[] changedBlocksPerSection;

    @Shadow
    @Final
    private LevelHeightAccessor levelHeightAccessor;

    @Unique
    private FullChunkStatus silkard$oldFullStatus;

    @Unique
    private FullChunkStatus silkard$newFullStatus;

    // CraftBukkit start
    @Override
    public LevelChunk getFullChunkNow() {
        // Note: We use the oldTicketLevel for isLoaded checks.
        if (!ChunkLevel.fullStatus(this.oldTicketLevel).isOrAfter(FullChunkStatus.FULL)) return null;
        return this.getFullChunkNowUnchecked();
    }

    @Override
    public LevelChunk getFullChunkNowUnchecked() {
        net.minecraft.world.level.chunk.ChunkAccess ret = ((ChunkHolder) (Object) this).getChunkIfPresent(net.minecraft.world.level.chunk.status.ChunkStatus.FULL);
        return ret instanceof LevelChunk levelChunk ? levelChunk : null; // CraftBukkit - 26.3 getChunkIfPresent
    }

    // ChunkUnloadEvent: called before the chunk is unloaded, the chunk can still be modified by plugins.
    @Override
    public void callEventIfUnloading(ChunkMap chunkmap) {
        FullChunkStatus oldFullChunkStatus = ChunkLevel.fullStatus(this.oldTicketLevel);
        FullChunkStatus newFullChunkStatus = ChunkLevel.fullStatus(this.ticketLevel);
        boolean oldIsFull = oldFullChunkStatus.isOrAfter(FullChunkStatus.FULL);
        boolean newIsFull = newFullChunkStatus.isOrAfter(FullChunkStatus.FULL);
        if (oldIsFull && !newIsFull) {
            ((ChunkHolder) (Object) this).getFullChunkFuture().thenAccept((either) -> {
                LevelChunk levelchunk = either.orElse(null);
                if (levelchunk != null) {
                    ((ContextChunkMap) chunkmap).silkard$callbackExecutor().execute(() -> {
                        // Tick lists are stored back inside the chunk once it becomes inaccessible,
                        // which may happen deferred; mark unsaved now.
                        levelchunk.markUnsaved();
                        ((ContextLevelChunk) levelchunk).unloadCallback();
                    });
                }
            }).exceptionally((throwable) -> {
                MinecraftServer.LOGGER.error("Failed to schedule unload callback for chunk " + (Object) this, throwable);
                return null;
            });

            // Run callback right away if the future was already done
            ((ContextChunkMap) chunkmap).silkard$callbackExecutor().run();
        }
    }
    // CraftBukkit end

    // SPIGOT-6086, SPIGOT-6296
    @Inject(method = "blockChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelHeightAccessor;getSectionIndex(I)I", shift = At.Shift.AFTER), cancellable = true)
    private void silkard$validateSectionIndex(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        int i = this.levelHeightAccessor.getSectionIndex(pos.getY());
        if (i < 0 || i >= this.changedBlocksPerSection.length) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateFutures", at = @At("HEAD"))
    private void silkard$captureFullStatus(ChunkMap chunkmap, Executor executor, CallbackInfo ci) {
        this.silkard$oldFullStatus = ChunkLevel.fullStatus(this.oldTicketLevel);
        this.silkard$newFullStatus = ChunkLevel.fullStatus(this.ticketLevel);
    }

    // ChunkLoadEvent: called after the chunk is loaded, the chunk is ready to be modified by plugins.
    @Inject(method = "updateFutures", at = @At("TAIL"))
    private void silkard$callChunkLoadEvent(ChunkMap chunkmap, Executor executor, CallbackInfo ci) {
        if (!this.silkard$oldFullStatus.isOrAfter(FullChunkStatus.FULL) && this.silkard$newFullStatus.isOrAfter(FullChunkStatus.FULL)) {
            ((ChunkHolder) (Object) this).getFullChunkFuture().thenAccept((either) -> {
                LevelChunk levelchunk = either.orElse(null);
                if (levelchunk != null) {
                    ((ContextChunkMap) chunkmap).silkard$callbackExecutor().execute(() -> ((ContextLevelChunk) levelchunk).loadCallback());
                }
            }).exceptionally((throwable) -> {
                MinecraftServer.LOGGER.error("Failed to schedule load callback for chunk " + (Object) this, throwable);
                return null;
            });

            // Run callback right away if the future was already done
            ((ContextChunkMap) chunkmap).silkard$callbackExecutor().run();
        }
    }
}
