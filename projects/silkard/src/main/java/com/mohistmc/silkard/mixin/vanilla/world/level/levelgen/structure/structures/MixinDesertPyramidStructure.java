package com.mohistmc.silkard.mixin.vanilla.world.level.levelgen.structure.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.block.CraftBrushableBlock;
import org.bukkit.craftbukkit.util.TransformerGeneratorAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(DesertPyramidStructure.class)
public class MixinDesertPyramidStructure {

    // CraftBukkit start - place brushable block via craft state
    @Inject(method = "placeSuspiciousSand", at = @At("HEAD"), cancellable = true)
    private static void silkard$placeSuspiciousSand(BoundingBox chunkBB, WorldGenLevel level, BlockPos position, CallbackInfo ci) {
        if (level instanceof TransformerGeneratorAccess transformerAccess) {
            CraftBrushableBlock brushableState = (CraftBrushableBlock) CraftBlockStates.getBlockState(level, position, Blocks.SUSPICIOUS_SAND.defaultBlockState(), null);
            brushableState.setLootTable(CraftLootTable.minecraftToBukkit(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY));
            brushableState.setSeed(position.asLong());
            transformerAccess.setCraftBlock(position, brushableState, 2);
            ci.cancel();
        }
    }
    // CraftBukkit end
}
