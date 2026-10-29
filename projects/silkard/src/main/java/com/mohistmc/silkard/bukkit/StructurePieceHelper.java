package com.mohistmc.silkard.bukkit;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.util.TransformerGeneratorAccess;
import org.bukkit.entity.EntityType;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
public final class StructurePieceHelper {

    private StructurePieceHelper() {
    }

    public static boolean placeCraftBlockEntity(ServerLevelAccessor level, BlockPos position, CraftBlockEntityState<?> craftState, int flags) {
        if (level instanceof TransformerGeneratorAccess transformerAccess) {
            return transformerAccess.setCraftBlock(position, craftState, flags);
        }
        boolean result = level.setBlock(position, craftState.getHandle(), flags);
        BlockEntity blockEntity = level.getBlockEntity(position);
        if (blockEntity != null) {
            blockEntity.loadWithComponents(craftState.getSnapshotInput());
        }
        return result;
    }

    public static void placeCraftSpawner(ServerLevelAccessor level, BlockPos position, EntityType entityType, int flags) {
        CraftCreatureSpawner spawner = (CraftCreatureSpawner) CraftBlockStates.getBlockState(level, position, Blocks.SPAWNER.defaultBlockState(), null);
        spawner.setSpawnedType(entityType);
        placeCraftBlockEntity(level, position, spawner, flags);
    }

    public static void setCraftLootTable(ServerLevelAccessor level, BlockPos position, RandomSource random, ResourceKey<LootTable> lootTableKey) {
        BlockEntity blockEntity = level.getBlockEntity(position);
        if (blockEntity instanceof RandomizableContainerBlockEntity container) {
            container.setLootTable(lootTableKey);
            container.setLootTableSeed(random.nextLong());
            if (level instanceof TransformerGeneratorAccess transformerAccess) {
                transformerAccess.setCraftBlock(position, (CraftBlockState) CraftBlockStates.getBlockState(level, position, blockEntity.getBlockState(), container.saveWithFullMetadata(level.registryAccess())), 3);
            }
        }
    }
}
