package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.TryLaySpawnOnFluidNearLand;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(TryLaySpawnOnFluidNearLand.class)
public class MixinTryLaySpawnOnFluidNearLand {

    // CraftBukkit start - EntityChangeBlockEvent
    // 注: CB 取消时同时跳过音效; redirect 语义下仅跳过方块放置
    @Redirect(method = "lambda$create$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private static boolean silkard$laySpawnChangeBlock(ServerLevel level, BlockPos spawnPos, BlockState newState, @Local(argsOnly = true) LivingEntity body, @Local(argsOnly = true, ordinal = 2) MemoryAccessor pregnant) {
        if (!CraftEventFactory.callEntityChangeBlockEvent(body, spawnPos, newState)) {
            pregnant.erase();
            return false;
        }
        return level.setBlockAndUpdate(spawnPos, newState);
    }
    // CraftBukkit end
}
