package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockBrushEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(BrushableBlockEntity.class)
public class MixinBrushableBlockEntity {

    @Shadow
    private void dropContent(ServerLevel level, LivingEntity user, ItemStack brush) {
        throw new AssertionError();
    }

    // CraftBukkit start - BlockBrushEvent on dusting
    @Redirect(method = "brush", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$brushDusted(ServerLevel level, BlockPos pos, BlockState state, long time, ServerLevel levelArg, LivingEntity user, net.minecraft.core.Direction direction, ItemStack brush) {
        BlockBrushEvent event = CraftEventFactory.callBlockBrushEvent(level, pos, state, 3, (ServerPlayer) user);
        if (event.isCancelled()) {
            return false;
        }
        event.getNewState().update(true);
        return true;
    }
    // CraftBukkit end

    // CraftBukkit start - defer content drop and sound to the completed event
    @Redirect(method = "brushingCompleted", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BrushableBlockEntity;dropContent(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"))
    private void silkard$suppressDrop(BrushableBlockEntity self, ServerLevel level, LivingEntity user, ItemStack brush) {
    }

    @Redirect(method = "brushingCompleted", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;levelEvent(ILnet/minecraft/core/BlockPos;I)V"))
    private void silkard$suppressSound(ServerLevel level, int type, BlockPos pos, int data) {
    }

    @Redirect(method = "brushingCompleted", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$brushCompleted(ServerLevel level, BlockPos pos, BlockState state, ServerLevel levelArg, LivingEntity user, ItemStack brush) {
        BlockBrushEvent event = CraftEventFactory.callBlockBrushEvent(level, pos, state, 3, (ServerPlayer) user);
        if (event.isCancelled()) {
            return false;
        }
        this.dropContent(level, user, brush);
        level.levelEvent(3008, pos, Block.getId(((BlockEntity) (Object) this).getBlockState()));
        event.getNewState().update(true);
        return true;
    }
    // CraftBukkit end

    // CraftBukkit start - BlockDropItemEvent
    @Redirect(method = "dropContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$dropItem(ServerLevel level, Entity entity, ServerLevel levelArg, LivingEntity user, ItemStack brush) {
        if (user instanceof ServerPlayer serverPlayer) {
            org.bukkit.block.Block block = CraftBlock.at(level, ((BlockEntity) (Object) this).getBlockPos());
            CraftEventFactory.handleBlockDropItemEvent(block, block.getState(), serverPlayer, Arrays.asList((ItemEntity) entity));
            return true;
        }
        return level.addFreshEntity(entity);
    }
    // CraftBukkit end
}
