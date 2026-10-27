package com.mohistmc.silkard.mixin.vanilla.world.level.levelgen.structure;

import com.mohistmc.silkard.injected.world.level.levelgen.structure.ContextStructureStart;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer;
import org.bukkit.craftbukkit.util.CraftStructureTransformer;
import org.bukkit.craftbukkit.util.TransformerGeneratorAccess;
import org.bukkit.event.world.AsyncStructureGenerateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/4/2 17:31
 */
@Mixin(StructureStart.class)
public class MixinStructureStart implements ContextStructureStart {

    // CraftBukkit start
    private static final org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY = new org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry();
    public org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer persistentDataContainer = new org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer(DATA_TYPE_REGISTRY);
    public org.bukkit.event.world.AsyncStructureGenerateEvent.Cause generationEventCause = org.bukkit.event.world.AsyncStructureGenerateEvent.Cause.WORLD_GENERATION;
    // CraftBukkit end

    @Shadow private Structure structure;

    @Unique private TransformerGeneratorAccess silkard$transformerAccess;

    @Override
    public AsyncStructureGenerateEvent.Cause silkard_generationEventCause() {
        return generationEventCause;
    }

    @Override
    public DirtyCraftPersistentDataContainer silkard_persistentDataContainer() {
        return persistentDataContainer;
    }

    @Override
    public void silkard_setGenerationEventCause(AsyncStructureGenerateEvent.Cause generationEventCause) {
        this.generationEventCause = generationEventCause;
    }

    @Override
    public void silkard_setPersistentDataContainer(DirtyCraftPersistentDataContainer persistentDataContainer) {
        this.persistentDataContainer = persistentDataContainer;
    }

    // CraftBukkit start
    // Pieces are placed through a transformer so plugins can rewrite blocks and entities
    @Redirect(method = "placeInChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/StructurePiece;postProcess(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/core/BlockPos;)V"))
    private void silkard$transformPiece(StructurePiece piece, WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox chunkBB, ChunkPos chunkPos, BlockPos pos) {
        if (this.silkard$transformerAccess == null) {
            TransformerGeneratorAccess access = new TransformerGeneratorAccess();
            access.setHandle(level);
            access.setStructureTransformer(new CraftStructureTransformer(this.generationEventCause, level, structureManager, this.structure, chunkBB, chunkPos));
            this.silkard$transformerAccess = access;
        }
        piece.postProcess(this.silkard$transformerAccess, structureManager, generator, random, chunkBB, chunkPos, pos);
    }

    @Inject(method = "placeInChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/Structure;afterPlace(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/levelgen/structure/pieces/PiecesContainer;)V"))
    private void silkard$discardTransformer(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox chunkBB, ChunkPos chunkPos, CallbackInfo ci) {
        if (this.silkard$transformerAccess != null) {
            this.silkard$transformerAccess.getStructureTransformer().discard();
            this.silkard$transformerAccess = null;
        }
    }

    @Inject(method = "createTag", at = @At("RETURN"))
    private void silkard$savePersistentData(StructurePieceSerializationContext context, ChunkPos chunkPos, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = cir.getReturnValue();
        if (!this.persistentDataContainer.isEmpty()) {
            tag.put("StructureBukkitValues", this.persistentDataContainer.toTagCompound());
        }
    }
    // CraftBukkit end
}
