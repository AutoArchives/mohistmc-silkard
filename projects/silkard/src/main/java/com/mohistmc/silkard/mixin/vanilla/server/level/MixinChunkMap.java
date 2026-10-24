package com.mohistmc.silkard.mixin.vanilla.server.level;

import com.mohistmc.silkard.injected.server.level.ContextChunkMap;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ChunkMap.class)
public class MixinChunkMap implements ContextChunkMap {

    // CraftBukkit start - recursion-safe executor for Chunk loadCallback() and unloadCallback()
    public final com.mohistmc.silkard.bukkit.BukkitCallbackExecutor silkard$callbackExecutorField = new com.mohistmc.silkard.bukkit.BukkitCallbackExecutor();

    @Override
    public com.mohistmc.silkard.bukkit.BukkitCallbackExecutor silkard$callbackExecutor() {
        return silkard$callbackExecutorField;
    }
    // CraftBukkit end
}
