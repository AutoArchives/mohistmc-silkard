package com.mohistmc.silkard.mixin.vanilla.world.entity;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(net.minecraft.world.entity.Bucketable.class)
public interface MixinBucketable {

    // CraftBukkit start - fire PlayerBucketEntityEvent
    @Overwrite
    static <T extends LivingEntity & net.minecraft.world.entity.Bucketable> Optional<InteractionResult> bucketMobPickup(Player player, InteractionHand hand, T pickupEntity) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (itemstack.getItem() == Items.WATER_BUCKET && pickupEntity.isAlive()) {
            ItemStack itemstack1 = pickupEntity.getBucketItemStack();

            pickupEntity.saveToBucketTag(itemstack1);

            PlayerBucketEntityEvent playerBucketFishEvent = CraftEventFactory.callPlayerFishBucketEvent(pickupEntity, player, itemstack, itemstack1, hand);
            itemstack1 = CraftItemStack.asNMSCopy(playerBucketFishEvent.getEntityBucket());
            if (playerBucketFishEvent.isCancelled()) {
                ((ServerPlayer) player).containerMenu.sendAllDataToRemote();
                pickupEntity.getBukkitEntity().update((ServerPlayer) player);
                ((ContextEntity) (Object) pickupEntity).refreshEntityData((ServerPlayer) player);
                return Optional.of(InteractionResult.FAIL);
            }
            pickupEntity.playSound(pickupEntity.getPickupSound(), 1.0F, 1.0F);
            ItemStack itemstack2 = ItemUtils.createFilledResult(itemstack, player, itemstack1, false);

            player.setItemInHand(hand, itemstack2);
            ((ContextEntity) (Object) pickupEntity).discard(EntityRemoveEvent.Cause.PICKUP);
            return Optional.of(InteractionResult.SUCCESS);
        } else {
            return Optional.empty();
        }
    }
    // CraftBukkit end
}
