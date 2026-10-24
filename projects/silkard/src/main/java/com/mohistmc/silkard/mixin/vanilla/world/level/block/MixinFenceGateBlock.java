package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(FenceGateBlock.class)
public class MixinFenceGateBlock {

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockRedstoneEvent
     */
    @Overwrite
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        if (!level.isClientSide()) {
            boolean hasPower = level.hasNeighborSignal(pos);
            // CraftBukkit start
            boolean oldPowered = state.getValue(FenceGateBlock.POWERED);
            if (oldPowered != hasPower) {
                int newPower = hasPower ? 15 : 0;
                int oldPower = oldPowered ? 15 : 0;
                org.bukkit.block.Block bukkitBlock = CraftBlock.at(level, pos);
                BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bukkitBlock, oldPower, newPower);
                level.getCraftServer().getPluginManager().callEvent(eventRedstone);
                hasPower = eventRedstone.getNewCurrent() > 0;
            }
            // CraftBukkit end

            if (state.getValue(FenceGateBlock.POWERED) != hasPower) {
                level.setBlock(pos, state.setValue(FenceGateBlock.POWERED, hasPower).setValue(FenceGateBlock.OPEN, hasPower), 2);
                if (state.getValue(FenceGateBlock.OPEN) != hasPower) {
                    level.playSound(
                            null,
                            pos,
                            hasPower ? this.type.fenceGateOpen() : this.type.fenceGateClose(),
                            net.minecraft.sounds.SoundSource.BLOCKS,
                            1.0F,
                            level.getRandom().nextFloat() * 0.1F + 0.9F
                    );
                    level.gameEvent(null, hasPower ? net.minecraft.world.level.gameevent.GameEvent.BLOCK_OPEN : net.minecraft.world.level.gameevent.GameEvent.BLOCK_CLOSE, pos);
                }
            }
        }
    }

    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    private net.minecraft.world.level.block.state.properties.WoodType type;
}
