package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(CommandBlock.class)
public class MixinCommandBlock {

    // CraftBukkit start
    @ModifyVariable(method = "setPoweredAndUpdate", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private boolean silkard$commandBlockRedstone(boolean isPowered, Level level, BlockPos pos, CommandBlockEntity commandBlock) {
        int oldCurrent = commandBlock.isPowered() ? 15 : 0;
        int newCurrent = isPowered ? 15 : 0;

        return CraftEventFactory.callRedstoneChange(level, pos, oldCurrent, newCurrent).getNewCurrent() > 0;
    }
    // CraftBukkit end
}
