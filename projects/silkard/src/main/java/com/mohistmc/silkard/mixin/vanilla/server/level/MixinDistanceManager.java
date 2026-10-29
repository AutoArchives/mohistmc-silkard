package com.mohistmc.silkard.mixin.vanilla.server.level;

import com.mohistmc.silkard.injected.server.level.ContextChunkHolder;
import java.util.Set;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(DistanceManager.class)
public class MixinDistanceManager {

    @Shadow
    @Final
    protected Set<ChunkHolder> chunksToUpdateFutures;

    // SPIGOT-7780: call all chunk unload events before updateHighestAllowedStatus
    @Inject(method = "runAllUpdates", at = @At(value = "INVOKE", target = "Ljava/util/Set;isEmpty()Z", shift = At.Shift.AFTER))
    private void silkard$callUnloadEvents(ChunkMap chunkmap, CallbackInfoReturnable<Boolean> cir) {
        for (ChunkHolder chunkholder : this.chunksToUpdateFutures) {
            ((ContextChunkHolder) chunkholder).callEventIfUnloading(chunkmap);
        }
    }
}
