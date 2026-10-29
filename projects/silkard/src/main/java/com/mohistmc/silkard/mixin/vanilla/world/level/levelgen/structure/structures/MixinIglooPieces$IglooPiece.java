package com.mohistmc.silkard.mixin.vanilla.world.level.levelgen.structure.structures;

import com.mohistmc.silkard.bukkit.StructurePieceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.structure.structures.IglooPieces;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(IglooPieces.IglooPiece.class)
public class MixinIglooPieces$IglooPiece {

    // CraftBukkit start - ensure block transformation
    @Redirect(method = "handleDataMarker", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/ChestBlockEntity;setLootTable(Lnet/minecraft/resources/ResourceKey;J)V"))
    private void silkard$setCraftLootTable(ChestBlockEntity chest, ResourceKey<LootTable> lootTableKey, long seed, String markerId, BlockPos position, ServerLevelAccessor level, RandomSource random) {
        StructurePieceHelper.setCraftLootTable(level, position.below(), random, lootTableKey);
    }
    // CraftBukkit end
}
