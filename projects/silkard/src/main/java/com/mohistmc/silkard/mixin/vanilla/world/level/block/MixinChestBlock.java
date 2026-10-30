package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.bukkit.DoubleChestCombiner;
import com.mohistmc.silkard.injected.world.level.block.ContextChestBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ChestBlock.class)
public class MixinChestBlock implements ContextChestBlock {

    @Unique
    private static final DoubleChestCombiner SILKARD_COMBINER = new DoubleChestCombiner();

    // CraftBukkit start - double chest menu provider
    @Override
    public @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos, boolean ignoreObstructions) {
        return ((ChestBlock) (Object) this).combine(state, level, pos, ignoreObstructions).apply(SILKARD_COMBINER).orElse(null);
    }
    // CraftBukkit end
}
