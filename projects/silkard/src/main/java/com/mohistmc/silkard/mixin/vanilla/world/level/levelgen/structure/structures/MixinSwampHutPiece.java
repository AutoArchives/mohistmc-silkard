package com.mohistmc.silkard.mixin.vanilla.world.level.levelgen.structure.structures;

import com.mohistmc.silkard.injected.world.level.ContextServerLevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.structures.SwampHutPiece;
import net.minecraft.world.level.ChunkPos;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(SwampHutPiece.class)
public class MixinSwampHutPiece {

    // CraftBukkit start - add SpawnReason
    @Redirect(method = "postProcess", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void silkard$spawnWitch(WorldGenLevel instance, Entity entity, WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox chunkBB, ChunkPos chunkPos, BlockPos pos) {
        ((ContextServerLevelAccessor) level).addFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.CHUNK_GEN);
    }

    @Redirect(method = "spawnCat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerLevelAccessor;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void silkard$spawnCat(ServerLevelAccessor instance, Entity entity, ServerLevelAccessor level, BoundingBox chunkBB) {
        ((ContextServerLevelAccessor) level).addFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.CHUNK_GEN);
    }
    // CraftBukkit end
}
