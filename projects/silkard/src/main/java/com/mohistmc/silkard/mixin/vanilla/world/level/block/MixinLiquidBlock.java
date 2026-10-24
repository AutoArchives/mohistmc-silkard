package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(LiquidBlock.class)
public class MixinLiquidBlock {

    @Shadow
    private void fizz(net.minecraft.world.level.LevelAccessor level, BlockPos pos) {
        throw new AssertionError();
    }

    // CraftBukkit start - BlockFormEvent when lava meets water (obsidian/cobblestone) or blue ice (basalt)
    @Redirect(method = "shouldSpreadLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0))
    private boolean silkard$meetWaterFormEvent(Level level, BlockPos pos, BlockState state) {
        if (CraftEventFactory.handleBlockFormEvent(level, pos, state)) {
            this.fizz(level, pos);
            return true;
        }
        return false;
    }

    @Redirect(method = "shouldSpreadLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1))
    private boolean silkard$soulSoilFormEvent(Level level, BlockPos pos, BlockState state) {
        if (CraftEventFactory.handleBlockFormEvent(level, pos, state)) {
            this.fizz(level, pos);
            return true;
        }
        return false;
    }
    // CraftBukkit end
}
