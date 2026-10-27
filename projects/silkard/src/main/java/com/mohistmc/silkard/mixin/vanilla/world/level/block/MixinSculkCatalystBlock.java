package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.block.ContextBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SculkCatalystBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SculkCatalystBlock.class)
public class MixinSculkCatalystBlock implements ContextBlock {

    @Shadow
    @Final
    private IntProvider xpRange;

    @Overwrite
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        // CraftBukkit - delegate to getExpDrop
    }

    public int getExpDrop(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        return dropExperience ? this.silkard$tryDropExperience(level, pos, tool, this.xpRange) : 0;
    }
}
