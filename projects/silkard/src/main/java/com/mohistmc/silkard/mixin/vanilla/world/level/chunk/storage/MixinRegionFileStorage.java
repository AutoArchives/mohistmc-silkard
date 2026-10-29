package com.mohistmc.silkard.mixin.vanilla.world.level.chunk.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(RegionFileStorage.class)
public abstract class MixinRegionFileStorage {

    @Invoker("regionPath")
    protected abstract Path silkard$regionPath(ChunkPos pos);

    // CraftBukkit start - SPIGOT-5680: don't create region files on read
    @Inject(method = "read", at = @At("HEAD"), cancellable = true)
    private void silkard$readExistingOnly(ChunkPos pos, CallbackInfoReturnable<CompoundTag> cir) {
        if (!Files.isRegularFile(this.silkard$regionPath(pos))) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "scanChunk", at = @At("HEAD"), cancellable = true)
    private void silkard$scanExistingOnly(ChunkPos pos, StreamTagVisitor visitor, CallbackInfo ci) {
        if (!Files.isRegularFile(this.silkard$regionPath(pos))) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
