package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DragonEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.event.block.BlockFromToEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(DragonEggBlock.class)
public class MixinDragonEggBlock {

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockFromToEvent on dragon egg teleport
     */
    @Overwrite
    private void teleport(BlockState state, Level level, BlockPos pos) {
        net.minecraft.util.RandomSource random = level.getRandom();
        net.minecraft.world.level.border.WorldBorder worldBorder = level.getWorldBorder();

        for (int i = 0; i < 1000; i++) {
            BlockPos testPos = pos.offset(
                    random.nextInt(16) - random.nextInt(16), random.nextInt(8) - random.nextInt(8), random.nextInt(16) - random.nextInt(16)
            );
            if (level.getBlockState(testPos).isAir()
                    && !level.getBlockState(testPos.below()).isAir()
                    && worldBorder.isWithinBounds(testPos)
                    && level.isInsideBuildHeight(testPos)) {
                // CraftBukkit start
                org.bukkit.block.Block from = ((com.mohistmc.silkard.injected.world.level.ContextLevel) level).getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
                org.bukkit.block.Block to = ((com.mohistmc.silkard.injected.world.level.ContextLevel) level).getWorld().getBlockAt(testPos.getX(), testPos.getY(), testPos.getZ());
                BlockFromToEvent event = new BlockFromToEvent(from, to);
                Bukkit.getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }

                testPos = new BlockPos(event.getToBlock().getX(), event.getToBlock().getY(), event.getToBlock().getZ());
                // CraftBukkit end
                int packedDiff = net.minecraft.util.BlockUtil.packDifferenceInPosition(pos, testPos, 16, 8, 16);
                level.levelEvent(2015, pos, packedDiff);
                level.setBlock(testPos, state, 2);
                level.removeBlock(pos, false);
                return;
            }
        }
    }
}
