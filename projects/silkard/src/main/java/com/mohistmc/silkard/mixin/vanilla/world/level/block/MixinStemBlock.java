package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(net.minecraft.world.level.block.StemBlock.class)
public class MixinStemBlock {

    // CraftBukkit start - BlockGrowEvent
    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean silkard$stemAgeGrowth(ServerLevel level, BlockPos pos, BlockState state, int flags) {
        return CraftEventFactory.handleBlockGrowEvent(level, pos, state, flags);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$stemFruitGrowth(ServerLevel level, BlockPos pos, BlockState state) {
        return CraftEventFactory.handleBlockGrowEvent(level, pos, state);
    }
    // CraftBukkit end
}
