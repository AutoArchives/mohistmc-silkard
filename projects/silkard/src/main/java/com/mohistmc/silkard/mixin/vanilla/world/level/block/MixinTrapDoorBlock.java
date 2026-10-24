package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.ContextLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(TrapDoorBlock.class)
public class MixinTrapDoorBlock {

    @Shadow
    protected void playSound(@Nullable Player player, Level level, BlockPos pos, boolean isOpen) {
        throw new AssertionError();
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockRedstoneEvent
     */
    @Overwrite
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        if (!level.isClientSide()) {
            boolean signal = level.hasNeighborSignal(pos);
            if (signal != state.getValue(TrapDoorBlock.POWERED)) {
                // CraftBukkit start
                org.bukkit.block.Block bblock = ((ContextLevel) level).getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());

                int power = bblock.getBlockPower();
                int oldPower = state.getValue(TrapDoorBlock.OPEN) ? 15 : 0;

                if (oldPower == 0 ^ power == 0 || block.defaultBlockState().isSignalSource()) {
                    BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bblock, oldPower, power);
                    level.getCraftServer().getPluginManager().callEvent(eventRedstone);
                    signal = eventRedstone.getNewCurrent() > 0;
                }
                // CraftBukkit end
                if (state.getValue(TrapDoorBlock.OPEN) != signal) {
                    state = state.setValue(TrapDoorBlock.OPEN, signal);
                    this.playSound(null, level, pos, signal);
                }

                level.setBlock(pos, state.setValue(TrapDoorBlock.POWERED, signal), 2);
                if (state.getValue(TrapDoorBlock.WATERLOGGED)) {
                    level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                }
            }
        }
    }
}
