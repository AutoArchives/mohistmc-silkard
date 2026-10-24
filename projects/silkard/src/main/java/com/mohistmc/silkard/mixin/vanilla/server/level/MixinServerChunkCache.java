package com.mohistmc.silkard.mixin.vanilla.server.level;

import com.mohistmc.silkard.injected.server.level.ContextServerChunkCache;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ServerChunkCache.class)
public class MixinServerChunkCache implements ContextServerChunkCache {

    // CraftBukkit start - properly implement isChunkLoaded
    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    private net.minecraft.server.level.ChunkMap chunkMap;

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
    private void silkard$runChunkCallbacks(CallbackInfoReturnable<Boolean> cir) {
        ((com.mohistmc.silkard.injected.server.level.ContextChunkMap) this.chunkMap).silkard$callbackExecutor().run();
    }
}
