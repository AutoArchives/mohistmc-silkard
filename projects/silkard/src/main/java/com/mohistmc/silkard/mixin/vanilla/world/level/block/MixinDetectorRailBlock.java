package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(DetectorRailBlock.class)
public abstract class MixinDetectorRailBlock extends Block {

    protected MixinDetectorRailBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // CraftBukkit start
    @Redirect(method = "checkPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$redstoneChange(Level level, BlockPos pos, BlockState newState) {
        boolean powered = newState.getValue(DetectorRailBlock.POWERED);
        BlockRedstoneEvent event = new BlockRedstoneEvent(CraftBlock.at(level, pos), powered ? 0 : 15, powered ? 15 : 0);
        level.getCraftServer().getPluginManager().callEvent(event);
        if (event.getNewCurrent() <= 0) {
            return true;
        }
        return level.setBlockAndUpdate(pos, newState);
    }
    // CraftBukkit end
}
