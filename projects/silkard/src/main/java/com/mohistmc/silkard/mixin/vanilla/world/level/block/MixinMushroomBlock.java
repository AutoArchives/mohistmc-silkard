package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.TreeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(MushroomBlock.class)
public class MixinMushroomBlock {

    // CraftBukkit start - BlockSpreadEvent (source pos 被随机游走循环覆盖后为最后一次游走点)
    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean silkard$mushroomSpread(ServerLevel level, BlockPos pos, BlockState state, int flags, @Local(argsOnly = true, ordinal = 0) BlockPos sourcePos) {
        return CraftEventFactory.handleBlockSpreadEvent(level, sourcePos, pos, state, flags);
    }

    // CraftBukkit - set TreeType before huge mushroom growth
    @Inject(method = "growMushroom", at = @At("HEAD"))
    private void silkard$mushroomTreeType(ServerLevel level, BlockPos pos, BlockState state, net.minecraft.util.RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        com.mohistmc.silkard.bukkit.BukkitUtils.setTreeType((this.self() == net.minecraft.world.level.block.Blocks.BROWN_MUSHROOM) ? TreeType.BROWN_MUSHROOM : TreeType.RED_MUSHROOM); // CraftBukkit
    }
    // CraftBukkit end

    private net.minecraft.world.level.block.Block self() {
        return (net.minecraft.world.level.block.Block) (Object) this;
    }
}
