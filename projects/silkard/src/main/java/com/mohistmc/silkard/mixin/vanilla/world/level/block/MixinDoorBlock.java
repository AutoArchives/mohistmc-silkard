package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.ContextLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.bukkit.event.block.BlockRedstoneEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
// defaultBlockState() 由 Block 声明（public final），@Shadow 命中不了继承成员 → extends Block
@Mixin(DoorBlock.class)
public abstract class MixinDoorBlock extends net.minecraft.world.level.block.Block {

    protected MixinDoorBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties properties) {
        super(properties);
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockRedstoneEvent (double-half power)
     */
    @Overwrite
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        // CraftBukkit start
        BlockPos otherHalf = pos.relative(state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN);

        org.bukkit.World bworld = ((ContextLevel) level).getWorld();
        org.bukkit.block.Block bukkitBlock = bworld.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        org.bukkit.block.Block blockTop = bworld.getBlockAt(otherHalf.getX(), otherHalf.getY(), otherHalf.getZ());

        int power = bukkitBlock.getBlockPower();
        int powerTop = blockTop.getBlockPower();
        if (powerTop > power) power = powerTop;
        int oldPower = state.getValue(DoorBlock.POWERED) ? 15 : 0;

        if (oldPower == 0 ^ power == 0) {
            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bukkitBlock, oldPower, power);
            level.getCraftServer().getPluginManager().callEvent(eventRedstone);

            boolean flag1 = eventRedstone.getNewCurrent() > 0;
            // CraftBukkit end
            if (flag1 != state.getValue(DoorBlock.OPEN)) {
                this.playSound(null, level, pos, flag1);
                level.gameEvent(null, flag1 ? net.minecraft.world.level.gameevent.GameEvent.BLOCK_OPEN : net.minecraft.world.level.gameevent.GameEvent.BLOCK_CLOSE, pos);
            }

            level.setBlock(pos, state.setValue(DoorBlock.POWERED, flag1).setValue(DoorBlock.OPEN, flag1), 2);
        }
    }

    @Shadow
    private void playSound(@Nullable Entity entity, Level level, BlockPos pos, boolean open) {
        throw new AssertionError();
    }

}
