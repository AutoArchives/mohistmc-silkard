package com.mohistmc.silkard.mixin.vanilla.world.level.levelgen.structure.structures;

import com.mohistmc.silkard.bukkit.StructurePieceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinPieces;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.block.CraftChest;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(OceanRuinPieces.OceanRuinPiece.class)
public class MixinOceanRuinPieces$OceanRuinPiece {

    @Shadow
    @Final
    private boolean isLarge;

    // CraftBukkit start - transform block to ensure loot table is accessible
    @Inject(method = "handleDataMarker", at = @At("HEAD"), cancellable = true)
    private void silkard$handleChest(String markerId, BlockPos position, ServerLevelAccessor level, RandomSource random, BoundingBox chunkBB, CallbackInfo ci) {
        if ("chest".equals(markerId)) {
            BlockState state = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, level.getFluidState(position).is(FluidTags.WATER));
            CraftChest craftChest = (CraftChest) CraftBlockStates.getBlockState(level, position, state, null);
            craftChest.setSeed(random.nextLong());
            craftChest.setLootTable(CraftLootTable.minecraftToBukkit(this.isLarge ? BuiltInLootTables.UNDERWATER_RUIN_BIG : BuiltInLootTables.UNDERWATER_RUIN_SMALL));
            StructurePieceHelper.placeCraftBlockEntity(level, position, craftChest, 2);
            ci.cancel();
        }
    }
    // CraftBukkit end
}
