package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.block.LeavesDecayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(LeavesBlock.class)
public class MixinLeavesBlock {

    // CraftBukkit start - LeavesDecayEvent
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    protected void silkard$leavesDecay(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (this.decaying(state)) {
            LeavesDecayEvent event = new LeavesDecayEvent(((com.mohistmc.silkard.injected.world.level.ContextLevel) level).getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()));
            level.getCraftServer().getPluginManager().callEvent(event);
            if (event.isCancelled() || level.getBlockState(pos).getBlock() != (net.minecraft.world.level.block.Block) (Object) this) {
                ci.cancel();
            }
        }
    }
    // CraftBukkit end

    @org.spongepowered.asm.mixin.Shadow
    protected boolean decaying(BlockState state) {
        throw new AssertionError();
    }
}
