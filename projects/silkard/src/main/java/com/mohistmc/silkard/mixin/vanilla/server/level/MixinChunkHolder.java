package com.mohistmc.silkard.mixin.vanilla.server.level;

import com.mohistmc.silkard.injected.server.level.ContextChunkHolder;
import net.minecraft.server.level.ChunkHolder;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ChunkHolder.class)
public class MixinChunkHolder implements ContextChunkHolder {

    // CraftBukkit start
    @org.spongepowered.asm.mixin.Shadow
    private int oldTicketLevel;

    @Override
    public net.minecraft.world.level.chunk.LevelChunk getFullChunkNow() {
        // Note: We use the oldTicketLevel for isLoaded checks.
        if (!net.minecraft.server.level.ChunkLevel.fullStatus(this.oldTicketLevel).isOrAfter(net.minecraft.server.level.FullChunkStatus.FULL)) return null;
        return this.getFullChunkNowUnchecked();
    }

    @Override
    public net.minecraft.world.level.chunk.LevelChunk getFullChunkNowUnchecked() {
        net.minecraft.world.level.chunk.ChunkAccess ret = ((ChunkHolder) (Object) this).getChunkIfPresent(net.minecraft.world.level.chunk.status.ChunkStatus.FULL);
        return ret instanceof net.minecraft.world.level.chunk.LevelChunk levelChunk ? levelChunk : null; // CraftBukkit - 26.3 getChunkIfPresent
    }
    // CraftBukkit end
}
