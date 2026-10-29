package com.mohistmc.silkard.mixin.vanilla.world.level.levelgen.structure.structures;

import com.mohistmc.silkard.bukkit.StructurePieceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftPieces;
import org.bukkit.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(MineshaftPieces.MineShaftCorridor.class)
public class MixinMineshaftPieces$MineShaftCorridor {

    // CraftBukkit start - place spawner via craft state
    @Redirect(method = "postProcess", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean silkard$placeSpawner(WorldGenLevel level, BlockPos position, BlockState state, int flags) {
        StructurePieceHelper.placeCraftSpawner(level, position, EntityType.CAVE_SPIDER, 2);
        return true;
    }

    @Redirect(method = "postProcess", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/level/WorldGenLevel;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private BlockEntity silkard$skipBlockEntity(WorldGenLevel level, BlockPos position) {
        return null;
    }
    // CraftBukkit end
}
