package com.mohistmc.silkard.mixin.vanilla.world.level.chunk.status;

import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ChunkStatusTasks.class)
public class MixinChunkStatusTasks {

    @Inject(method = "generateStructureStarts", at = @At("HEAD"))
    private static void silkard_generate(CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        // CraftBukkit
    }
}
