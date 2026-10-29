package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.core.dispenser.ContextDefaultDispenseItemBehavior;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DropperBlock.class)
public abstract class MixinDropperBlock {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    private static DispenseItemBehavior DISPENSE_BEHAVIOUR;

    @Unique
    private static void silkard$initDropper() {
        // CraftBukkit - new DefaultDispenseItemBehavior(true)
        ((ContextDefaultDispenseItemBehavior) DISPENSE_BEHAVIOUR).silkard$dropper(true);
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void silkard$clinit(CallbackInfo ci) {
        silkard$initDropper();
    }

    // CraftBukkit start - InventoryMoveItemEvent when pushing items into other inventories
    @Redirect(method = "dispenseFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack silkard$addItem(Container dropper, Container container, ItemStack stack, Direction direction) {
        DispenserBlockEntity dropperEntity = (DispenserBlockEntity) dropper;
        ServerLevel level = (ServerLevel) dropperEntity.getLevel();
        CraftItemStack oitemstack = CraftItemStack.asCraftMirror(stack);

        org.bukkit.inventory.Inventory destinationInventory;
        // Have to special case large chests as they work oddly
        if (container instanceof CompoundContainer compoundContainer) {
            destinationInventory = new CraftInventoryDoubleChest(compoundContainer);
        } else {
            destinationInventory = container.getOwner().getInventory();
        }

        InventoryMoveItemEvent event = new InventoryMoveItemEvent(dropperEntity.getOwner().getInventory(), oitemstack, destinationInventory, true);
        level.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            // Keep the dropper contents untouched
            return stack;
        }

        ItemStack moved = HopperBlockEntity.addItem(dropper, container, CraftItemStack.asNMSCopy(event.getItem()), direction);
        // A changed item keeps the original stack in the dropper, matching CraftBukkit
        return event.getItem().equals(oitemstack) ? moved : stack;
    }
    // CraftBukkit end
}
