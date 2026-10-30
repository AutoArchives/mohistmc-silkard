package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.piglin;

import com.mohistmc.silkard.injected.world.entity.monster.piglin.ContextPiglin;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/5/1 18:14
 */
@Mixin(Piglin.class)
public class MixinPiglin implements ContextPiglin {

    // CraftBukkit start - Custom bartering and interest list
    public Set<Item> allowedBarterItems = new HashSet<>();
    public Set<Item> interestItems = new HashSet<>();
    // CraftBukkit end

    @Shadow
    protected void holdInMainHand(ItemStack stack) {
        throw new AssertionError();
    }

    @Override
    public void silkard$holdInMainHand(ItemStack stack) {
        this.holdInMainHand(stack);
    }

    public Set<Item> silkard$allowedBarterItems() {
        return allowedBarterItems;
    }

    public Set<Item> silkard$interestItems() {
        return interestItems;
    }

    // CraftBukkit start - persist the custom bartering and interest lists
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void silkard$saveBukkitLists(ValueOutput output, CallbackInfo ci) {
        ValueOutput.TypedOutputList<String> barterList = output.list("Bukkit.BarterList", Codec.STRING);
        allowedBarterItems.stream().map(BuiltInRegistries.ITEM::getKey).map(Identifier::toString).forEach(barterList::add);
        ValueOutput.TypedOutputList<String> interestList = output.list("Bukkit.InterestList", Codec.STRING);
        interestItems.stream().map(BuiltInRegistries.ITEM::getKey).map(Identifier::toString).forEach(interestList::add);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void silkard$loadBukkitLists(ValueInput input, CallbackInfo ci) {
        input.list("Bukkit.BarterList", Codec.STRING).ifPresent(list -> {
            this.allowedBarterItems = list.stream().map(Identifier::tryParse).map(BuiltInRegistries.ITEM::getValue).collect(Collectors.toCollection(HashSet::new));
        });
        input.list("Bukkit.InterestList", Codec.STRING).ifPresent(list -> {
            this.interestItems = list.stream().map(Identifier::tryParse).map(BuiltInRegistries.ITEM::getValue).collect(Collectors.toCollection(HashSet::new));
        });
    }
    // CraftBukkit end

    // CraftBukkit start - accept custom payment items
    @Redirect(method = "holdInOffHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private boolean silkard$isBarteringItem(ItemStack stack, Object item) {
        if (item == PiglinAi.BARTERING_ITEM) {
            return stack.is((Item) item) || this.allowedBarterItems.contains(stack.getItem());
        }
        return stack.is((Item) item);
    }

    @Redirect(method = "canReplaceCurrentItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isLovedItem(Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 0))
    private boolean silkard$isLovedItemNew(ItemStack itemStack) {
        return itemStack.is(ItemTags.PIGLIN_LOVED) || this.interestItems.contains(itemStack.getItem()) || this.allowedBarterItems.contains(itemStack.getItem());
    }

    @Redirect(method = "canReplaceCurrentItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isLovedItem(Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 1))
    private boolean silkard$isLovedItemCurrent(ItemStack itemStack) {
        return itemStack.is(ItemTags.PIGLIN_LOVED) || this.interestItems.contains(itemStack.getItem()) || this.allowedBarterItems.contains(itemStack.getItem());
    }
    // CraftBukkit end
}
