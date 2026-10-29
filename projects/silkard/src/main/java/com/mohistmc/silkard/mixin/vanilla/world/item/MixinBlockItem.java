package com.mohistmc.silkard.mixin.vanilla.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(BlockItem.class)
public class MixinBlockItem {

    @Unique
    private org.bukkit.block.BlockState silkard$replacedBlockState;

    // CraftBukkit - special case for water lilies and snow buckets
    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;getPlacementState(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private void silkard$recordReplaced(BlockPlaceContext placeContext, CallbackInfoReturnable<InteractionResult> cir, @Local(ordinal = 1) BlockPlaceContext updatedPlaceContext) {
        if ((Object) this instanceof PlaceOnWaterBlockItem || (Object) this instanceof SolidBucketItem) {
            this.silkard$replacedBlockState = CraftBlockStates.getBlockState(updatedPlaceContext.getLevel(), updatedPlaceContext.getClickedPos());
        } else {
            this.silkard$replacedBlockState = null;
        }
    }

    // CraftBukkit - BlockPlaceEvent for replaced states
    @Inject(method = "place", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/level/block/Block;setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"), cancellable = true)
    private void silkard$blockPlace(BlockPlaceContext placeContext, CallbackInfoReturnable<InteractionResult> cir, @Local BlockPos pos, @Local Level level, @Local Player player, @Local(ordinal = 1) BlockPlaceContext updatedPlaceContext) {
        if (this.silkard$replacedBlockState != null && level instanceof ServerLevel serverLevel) {
            BlockPlaceEvent placeEvent = CraftEventFactory.callBlockPlaceEvent(serverLevel, player, updatedPlaceContext.getHand(), this.silkard$replacedBlockState, pos.getX(), pos.getY(), pos.getZ());

            if (placeEvent != null && (placeEvent.isCancelled() || !placeEvent.canBuild())) {
                this.silkard$replacedBlockState.update(true, false);

                if ((Object) this instanceof SolidBucketItem) {
                    ((ServerPlayer) player).getBukkitEntity().updateInventory();
                }
                cir.setReturnValue(InteractionResult.FAIL);
            }
        }
    }

    // CraftBukkit - BlockCanBuildEvent
    @Inject(method = "canPlace", at = @At("RETURN"), cancellable = true)
    private void silkard$canBuild(BlockPlaceContext context, BlockState stateForPlacement, CallbackInfoReturnable<Boolean> cir) {
        Player player = context.getPlayer();
        org.bukkit.entity.Player bukkitPlayer = player instanceof ServerPlayer ? (org.bukkit.entity.Player) player.getBukkitEntity() : null;
        BlockCanBuildEvent event = new BlockCanBuildEvent(CraftBlock.at(context.getLevel(), context.getClickedPos()), bukkitPlayer, CraftBlockData.fromData(stateForPlacement), cir.getReturnValue());
        context.getLevel().getCraftServer().getPluginManager().callEvent(event);
        cir.setReturnValue(event.isBuildable());
    }

    // CraftBukkit - block place sound handled by BlockPlaceEvent
    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void silkard$skipPlaceSound(Level level, Entity entity, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch) {
    }

    @Inject(method = "place", at = @At("RETURN"))
    private void silkard$reset(CallbackInfoReturnable<InteractionResult> cir) {
        this.silkard$replacedBlockState = null;
    }
}
