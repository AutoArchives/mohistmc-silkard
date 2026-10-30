package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(BambooStalkBlock.class)
public class MixinBambooStalkBlock {

    @Unique
    private boolean silkard$shouldUpdateOthers;
    @Unique
    private BlockState silkard$belowState;
    @Unique
    private BlockState silkard$twoBelowState;

    @Inject(method = "growBamboo", at = @At("HEAD"))
    private void silkard$reset(BlockState state, Level level, BlockPos pos, RandomSource random, int height, CallbackInfo ci) {
        this.silkard$shouldUpdateOthers = false;
    }

    // CraftBukkit - If the BlockSpreadEvent was cancelled, we have no bamboo here
    @Redirect(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
    private Comparable<?> silkard$isBamboo(BlockState instance, Property<?> property, ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        return instance.is(Blocks.BAMBOO) ? instance.getValue(BambooStalkBlock.STAGE) : 1;
    }

    @Redirect(method = "growBamboo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0))
    private boolean silkard$deferBelow(Level instance, BlockPos callPos, BlockState callState, BlockState state, Level level, BlockPos pos, RandomSource random, int height) {
        this.silkard$shouldUpdateOthers = true;
        this.silkard$belowState = callState;
        return true;
    }

    @Redirect(method = "growBamboo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1))
    private boolean silkard$deferTwoBelow(Level instance, BlockPos callPos, BlockState callState, BlockState state, Level level, BlockPos pos, RandomSource random, int height) {
        this.silkard$twoBelowState = callState;
        return true;
    }

    @Redirect(method = "growBamboo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 2))
    private boolean silkard$growBamboo(Level instance, BlockPos callPos, BlockState callState, BlockState state, Level level, BlockPos pos, RandomSource random, int height) {
        boolean result = CraftEventFactory.handleBlockSpreadEvent(level, pos, callPos, callState, 3);
        if (result && this.silkard$shouldUpdateOthers) {
            level.setBlockAndUpdate(pos.below(), this.silkard$belowState);
            level.setBlockAndUpdate(pos.below(2), this.silkard$twoBelowState);
        }
        return result;
    }
}
