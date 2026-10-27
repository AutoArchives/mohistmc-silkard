package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(TripWireHookBlock.class)
public abstract class MixinTripWireHookBlock extends Block {

    protected MixinTripWireHookBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // CraftBukkit start
    @Inject(method = "calculateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TripWireHookBlock;emitState(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ZZZZ)V", ordinal = 1), cancellable = true)
    private static void silkard$redstoneChange(Level level, BlockPos pos, BlockState state, boolean flag1, boolean flag2, int depth, BlockState neighbourState, CallbackInfo ci) {
        BlockRedstoneEvent event = new BlockRedstoneEvent(CraftBlock.at(level, pos), 15, 0);
        level.getCraftServer().getPluginManager().callEvent(event);
        if (event.getNewCurrent() > 0) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
