package com.mohistmc.silkard.bukkit;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.world.PortalCreateEvent;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
public final class EndPlatformHelper {

    private EndPlatformHelper() {
    }

    // CraftBukkit start
    public static void createEndPlatform(ServerLevelAccessor level, BlockPos origin, boolean dropResources, Entity entity) {
        BlockStateListPopulator blockList = new BlockStateListPopulator(level);
        BlockPos.MutableBlockPos mutable = origin.mutable();

        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                for (int k = -1; k <= 3; ++k) {
                    BlockPos pos = mutable.set(origin).move(j, k, i);
                    Block block = k == -1 ? Blocks.OBSIDIAN : Blocks.AIR;

                    if (!blockList.getBlockState(pos).is(block)) {
                        if (dropResources) {
                            blockList.destroyBlock(pos, true, null);
                        }

                        blockList.setBlock(pos, block.defaultBlockState(), 3);
                    }
                }
            }
        }

        // SPIGOT-7746: Entity will only be null during world generation, which is async, so just generate without event
        if (entity != null) {
            World world = level.getLevel().getWorld();
            PortalCreateEvent event = new PortalCreateEvent((List<BlockState>) (List<?>) blockList.getList(), world, entity.getBukkitEntity(), PortalCreateEvent.CreateReason.END_PLATFORM);

            level.getLevel().getCraftServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
        }

        // SPIGOT-7856: End platform not dropping items after replacing blocks
        if (dropResources) {
            blockList.getList().forEach(state -> level.destroyBlock(state.getPosition(), true, null));
        }
        blockList.updateList();
    }
    // CraftBukkit end
}
