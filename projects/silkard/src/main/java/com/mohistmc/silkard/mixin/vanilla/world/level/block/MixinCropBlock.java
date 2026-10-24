package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(CropBlock.class)
public class MixinCropBlock {

    // CraftBukkit start - BlockGrowEvent
    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean silkard$randomGrowth(ServerLevel level, BlockPos pos, BlockState state, int flags) {
        return CraftEventFactory.handleBlockGrowEvent(level, pos, state, flags);
    }

    @Redirect(method = "growCrops", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean silkard$bonemealGrowth(Level level, BlockPos pos, BlockState state, int flags) {
        return CraftEventFactory.handleBlockGrowEvent(level, pos, state, flags);
    }

    // CraftBukkit - EntityChangeBlockEvent when Ravager eats crops
    @Redirect(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$ravagerEat(ServerLevel level, BlockPos pos, boolean drop, Entity entity) {
        if (CraftEventFactory.callEntityChangeBlockEvent(entity, pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), !level.getGameRules().get(net.minecraft.world.level.gamerules.GameRules.MOB_GRIEFING))) {
            return level.destroyBlock(pos, drop, entity);
        }
        return false;
    }
    // CraftBukkit end
}
