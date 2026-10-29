package com.mohistmc.silkard.mixin.vanilla.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(StandingAndWallBlockItem.class)
public class MixinStandingAndWallBlockItem {

    // CraftBukkit - BlockCanBuildEvent
    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelReader;isUnobstructed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Z"))
    private boolean silkard$canBuild(LevelReader levelReader, BlockState blockState, BlockPos blockPos, CollisionContext collisionContext,
                                    @Local(argsOnly = true) BlockPlaceContext context) {
        boolean defaultReturn = levelReader.isUnobstructed(blockState, blockPos, collisionContext);
        org.bukkit.entity.Player player = (context.getPlayer() instanceof ServerPlayer serverPlayer) ? (org.bukkit.entity.Player) serverPlayer.getBukkitEntity() : null;

        BlockCanBuildEvent event = new BlockCanBuildEvent(CraftBlock.at(context.getLevel(), blockPos), player, CraftBlockData.fromData(blockState), defaultReturn);
        context.getLevel().getCraftServer().getPluginManager().callEvent(event);

        return event.isBuildable();
    }
}
