package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.piglin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.injected.world.entity.monster.piglin.ContextPiglin;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(PiglinAi.class)
public abstract class MixinPiglinAi {

    @Shadow
    protected static boolean isLovedItem(ItemStack itemStack) {
        throw new AssertionError();
    }

    @Shadow
    private static boolean isBarterCurrency(ItemStack itemStack) {
        throw new AssertionError();
    }

    @Shadow
    private static void stopWalking(Piglin body) {
        throw new AssertionError();
    }

    @Shadow
    private static ItemStack removeOneItemFromItemEntity(ItemEntity itemEntity) {
        throw new AssertionError();
    }

    @Shadow
    private static void holdInOffhand(ServerLevel level, Piglin body, ItemStack itemStack) {
        throw new AssertionError();
    }

    @Shadow
    private static void admireGoldItem(LivingEntity entity) {
        throw new AssertionError();
    }

    @Shadow
    private static boolean isFood(ItemStack itemStack) {
        throw new AssertionError();
    }

    @Shadow
    private static boolean hasEatenRecently(Piglin body) {
        throw new AssertionError();
    }

    @Shadow
    private static void eat(Piglin body) {
        throw new AssertionError();
    }

    @Shadow
    private static void putInInventory(Piglin body, ItemStack itemStack) {
        throw new AssertionError();
    }

    @Shadow
    private static void throwItems(Piglin body, List<ItemStack> items) {
        throw new AssertionError();
    }

    @Shadow
    private static List<ItemStack> getBarterResponseItems(Piglin body) {
        throw new AssertionError();
    }

    // CraftBukkit start - custom barter and interest items
    private static boolean silkard$isLovedItem(ItemStack itemStack, Piglin piglin) {
        if (isLovedItem(itemStack)) {
            return true;
        }
        Set<Item> interestItems = ((ContextPiglin) piglin).silkard$interestItems();
        Set<Item> allowedBarterItems = ((ContextPiglin) piglin).silkard$allowedBarterItems();
        return interestItems.contains(itemStack.getItem()) || allowedBarterItems.contains(itemStack.getItem());
    }

    private static boolean silkard$isBarterCurrency(ItemStack itemStack, Piglin piglin) {
        if (isBarterCurrency(itemStack)) {
            return true;
        }
        return ((ContextPiglin) piglin).silkard$allowedBarterItems().contains(itemStack.getItem());
    }
    // CraftBukkit end

    // CraftBukkit start - EntityPickupItemEvent
    @Overwrite
    protected static void pickUpItem(ServerLevel level, Piglin body, ItemEntity itemEntity) {
        stopWalking(body);
        ItemStack itemstack;
        if (itemEntity.getItem().is(Items.GOLD_NUGGET) && !CraftEventFactory.callEntityPickupItemEvent(body, itemEntity, 0, false).isCancelled()) {
            body.take(itemEntity, itemEntity.getItem().getCount());
            itemstack = itemEntity.getItem();
            itemEntity.discard(EntityRemoveEvent.Cause.PICKUP);
        } else if (!CraftEventFactory.callEntityPickupItemEvent(body, itemEntity, itemEntity.getItem().getCount() - 1, false).isCancelled()) {
            body.take(itemEntity, 1);
            itemstack = removeOneItemFromItemEntity(itemEntity);
        } else {
            return;
        }

        if (silkard$isLovedItem(itemstack, body)) {
            body.getBrain().eraseMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
            holdInOffhand(level, body, itemstack);
            admireGoldItem(body);
        } else if (isFood(itemstack) && !hasEatenRecently(body)) {
            eat(body);
        } else {
            boolean flag = !body.equipItemIfPossible(level, itemstack).equals(ItemStack.EMPTY);
            if (!flag) {
                putInInventory(body, itemstack);
            }
        }
    }

    @Redirect(method = "removeOneItemFromItemEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private static void silkard$discard(ItemEntity itemEntity) {
        itemEntity.discard(EntityRemoveEvent.Cause.PICKUP);
    }
    // CraftBukkit end

    // CraftBukkit start - PiglinBarterEvent and custom payment items
    @Overwrite
    protected static void stopHoldingOffHandItem(ServerLevel level, Piglin body, boolean barteringEnabled) {
        ItemStack itemstack = body.getItemInHand(InteractionHand.OFF_HAND);
        body.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        if (body.isAdult()) {
            boolean flag = silkard$isBarterCurrency(itemstack, body);
            if (barteringEnabled && flag) {
                PiglinBarterEvent event = CraftEventFactory.callPiglinBarterEvent(body, getBarterResponseItems(body), itemstack);
                if (!event.isCancelled()) {
                    throwItems(body, event.getOutcome().stream().map(CraftItemStack::asNMSCopy).collect(Collectors.toList()));
                }
            } else if (!flag) {
                boolean flag1 = !body.equipItemIfPossible(level, itemstack).isEmpty();
                if (!flag1) {
                    putInInventory(body, itemstack);
                }
            }
        } else {
            boolean flag2 = !body.equipItemIfPossible(level, itemstack).isEmpty();
            if (!flag2) {
                ItemStack itemstack1 = body.getMainHandItem();
                if (silkard$isLovedItem(itemstack1, body)) {
                    putInInventory(body, itemstack1);
                } else {
                    throwItems(body, Collections.singletonList(itemstack1));
                }
                ((MixinPiglin) (Object) body).silkard$holdInMainHand(itemstack);
            }
        }
    }

    @Redirect(method = "wantsToPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isBarterCurrency(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean silkard$wantsToPickupBarterCurrency(ItemStack itemStack, @Local(argsOnly = true) Piglin body) {
        return silkard$isBarterCurrency(itemStack, body);
    }

    @Redirect(method = "canAdmire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isBarterCurrency(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean silkard$canAdmireBarterCurrency(ItemStack itemStack, @Local(argsOnly = true) Piglin body) {
        return silkard$isBarterCurrency(itemStack, body);
    }

    @Redirect(method = "isNotHoldingLovedItemInOffHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isLovedItem(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean silkard$notHoldingLovedItem(ItemStack itemStack, @Local(argsOnly = true) Piglin body) {
        return silkard$isLovedItem(itemStack, body);
    }
    // CraftBukkit end
}
