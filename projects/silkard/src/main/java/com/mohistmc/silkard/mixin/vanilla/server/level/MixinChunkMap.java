package com.mohistmc.silkard.mixin.vanilla.server.level;

import com.mohistmc.silkard.injected.server.level.ContextChunkMap;
import java.util.function.BooleanSupplier;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(ChunkMap.class)
public class MixinChunkMap implements ContextChunkMap {

    @Shadow
    protected void tick(BooleanSupplier supplier) {
        throw new AssertionError();
    }

    // CraftBukkit start - recursion-safe executor for Chunk loadCallback() and unloadCallback()
    public final com.mohistmc.silkard.bukkit.BukkitCallbackExecutor silkard$callbackExecutorField = new com.mohistmc.silkard.bukkit.BukkitCallbackExecutor();

    @Override
    public com.mohistmc.silkard.bukkit.BukkitCallbackExecutor silkard$callbackExecutor() {
        return silkard$callbackExecutorField;
    }
    // CraftBukkit end

    // CraftBukkit start - expose the protected chunk tick for ServerChunkCache#purgeUnload
    @Override
    public void silkard$tick(BooleanSupplier supplier) {
        this.tick(supplier);
    }
    // CraftBukkit end
}
