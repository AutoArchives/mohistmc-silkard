package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.NotePlayEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(NoteBlock.class)
public class MixinNoteBlock {

    /**
     * @author Mgazul
     * @reason CraftBukkit - NotePlayEvent
     */
    @Overwrite
    private void playNote(@Nullable Entity source, BlockState state, Level level, BlockPos pos) {
        if (state.getValue(NoteBlock.INSTRUMENT).worksAboveNoteBlock() || level.getBlockState(pos.above()).isAir()) {
            // CraftBukkit start
            NotePlayEvent event = CraftEventFactory.callNotePlayEvent(level, pos, state.getValue(NoteBlock.INSTRUMENT), state.getValue(NoteBlock.NOTE));
            if (event.isCancelled()) {
                return;
            }
            // CraftBukkit end
            level.blockEvent(pos, (NoteBlock) (Object) this, 0, 0);
            level.gameEvent(source, net.minecraft.world.level.gameevent.GameEvent.NOTE_BLOCK_PLAY, pos);
        }
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - SPIGOT-5617: the note may have changed the block in the event
     */
    @Overwrite
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        boolean signal = level.hasNeighborSignal(pos);
        if (signal != state.getValue(NoteBlock.POWERED)) {
            if (signal) {
                this.playNote(null, state, level, pos);
                state = level.getBlockState(pos); // CraftBukkit - SPIGOT-5617
            }

            level.setBlockAndUpdate(pos, state.setValue(NoteBlock.POWERED, signal));
        }
    }
}
