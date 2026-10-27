package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(net.minecraft.world.level.block.CactusBlock.class)
public class MixinCactusBlock {

    // CraftBukkit start - BlockGrowEvent
    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$cactusGrowth(ServerLevel level, BlockPos pos, BlockState state) {
        return CraftEventFactory.handleBlockGrowEvent(level, pos, state);
    }
    // CraftBukkit end

    /**
     * @author Mgazul
     * @reason CraftBukkit - keep the block in the damage source so plugins get the cactus location
     */
    @Overwrite
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        entity.hurt(level.damageSources().cactus().directBlock(level, pos), 1.0F);
    }
}
