package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(HarvestFarmland.class)
public class MixinHarvestFarmland {

    // CraftBukkit start - EntityChangeBlockEvent
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$harvestChangeBlock(ServerLevel level, BlockPos pos, boolean drop, net.minecraft.world.entity.Entity entity, @Local(argsOnly = true) Villager body) {
        return CraftEventFactory.callEntityChangeBlockEvent(body, pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()) && level.destroyBlock(pos, drop, entity);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$plantChangeBlock(ServerLevel level, BlockPos pos, BlockState state, @Local(argsOnly = true) Villager body) {
        return CraftEventFactory.callEntityChangeBlockEvent(body, pos, state) && level.setBlockAndUpdate(pos, state);
    }
    // CraftBukkit end
}
