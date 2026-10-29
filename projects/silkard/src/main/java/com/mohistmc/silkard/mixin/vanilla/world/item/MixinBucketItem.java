package com.mohistmc.silkard.mixin.vanilla.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.DummyGeneratorAccess;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(BucketItem.class)
public class MixinBucketItem {

    @Unique
    private BlockPos silkard$clicked;
    @Unique
    private Direction silkard$direction;
    @Unique
    private ItemStack silkard$stack;
    @Unique
    private InteractionHand silkard$hand;
    @Unique
    private ItemStack silkard$filledStack;

    // CraftBukkit - pass clicked block state to emptyContents
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"))
    private BlockPos silkard$clickedPos(BlockHitResult hitResult) {
        this.silkard$clicked = hitResult.getBlockPos();
        return this.silkard$clicked;
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/BlockHitResult;getDirection()Lnet/minecraft/core/Direction;"))
    private Direction silkard$clickedFace(BlockHitResult hitResult) {
        this.silkard$direction = hitResult.getDirection();
        return this.silkard$direction;
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BucketItem;emptyContents(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;)Z"))
    private boolean silkard$emptyContents(BucketItem bucket, LivingEntity user, Level level, BlockPos pos, BlockHitResult hitResult, @Local ItemStack itemStack, @Local(argsOnly = true) InteractionHand hand) {
        this.silkard$stack = itemStack;
        this.silkard$hand = hand;
        return bucket.emptyContents(user, level, pos, hitResult);
    }

    // CraftBukkit - PlayerBucketFillEvent
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BucketPickup;pickupBlock(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack silkard$pickupBlock(BucketPickup pickup, LivingEntity player, LevelAccessor accessor, BlockPos pos, BlockState state, @Local(argsOnly = true) Level level, @Local(argsOnly = true) InteractionHand hand, @Local ItemStack itemStack) {
        ItemStack dummyFluid = pickup.pickupBlock(player, DummyGeneratorAccess.INSTANCE, pos, state);

        if (dummyFluid.isEmpty()) {
            return ItemStack.EMPTY;
        }
        PlayerBucketFillEvent event = CraftEventFactory.callPlayerBucketFillEvent(level, (Player) player, pos, pos, this.silkard$direction, itemStack, dummyFluid.getItem(), hand);

        if (event.isCancelled()) {
            ((ServerPlayer) player).connection.send(new ClientboundBlockUpdatePacket(level, pos));
            ((ServerPlayer) player).getBukkitEntity().updateInventory();
            return ItemStack.EMPTY;
        }
        this.silkard$filledStack = CraftItemStack.asNMSCopy(event.getItemStack());
        return pickup.pickupBlock(player, accessor, pos, state);
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/item/ItemUtils;createFilledResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack silkard$filledResult(ItemStack used, Player player, ItemStack taken) {
        return ItemUtils.createFilledResult(used, player, this.silkard$filledStack != null ? this.silkard$filledStack : taken);
    }

    // CraftBukkit - PlayerBucketEmptyEvent
    @Inject(method = "emptyContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z"), cancellable = true)
    private void silkard$bucketEmpty(LivingEntity user, Level level, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 2) boolean placeLiquid) {
        if (placeLiquid && user instanceof ServerPlayer serverPlayer && this.silkard$stack != null) {
            PlayerBucketEmptyEvent event = CraftEventFactory.callPlayerBucketEmptyEvent(level, serverPlayer, pos, this.silkard$clicked, this.silkard$direction, this.silkard$stack, this.silkard$hand);

            if (event.isCancelled()) {
                serverPlayer.connection.send(new ClientboundBlockUpdatePacket(level, pos));
                serverPlayer.getBukkitEntity().updateInventory();
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "use", at = @At("RETURN"))
    private void silkard$reset(CallbackInfoReturnable<InteractionResult> cir) {
        this.silkard$stack = null;
        this.silkard$hand = null;
        this.silkard$filledStack = null;
    }
}
