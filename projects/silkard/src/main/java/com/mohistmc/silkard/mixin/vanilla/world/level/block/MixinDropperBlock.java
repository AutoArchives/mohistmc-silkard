package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.core.dispenser.ContextDefaultDispenseItemBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
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

    // CraftBukkit - InventoryMoveItemEvent when pushing items into other inventories.
    // 不能用 @Overwrite：覆盖整个方法体会把其他 mod（fabric-transfer-api-v1）在
    // dispenseFrom 里的 INVOKE 注入点一起抹掉，导致它们 INJECT_PREPARE 阶段抛
    // "cannot inject into ... merged by MixinDropperBlock"。
    @Redirect(method = "dispenseFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack silkard$addItem(Container source, Container destination, ItemStack stack, Direction direction,
                       ServerLevel level) {
        CraftItemStack oitemstack = CraftItemStack.asCraftMirror(stack.copyWithCount(1));

        org.bukkit.inventory.Inventory destinationInventory;
        // Have to special case large chests as they work oddly
        if (destination instanceof CompoundContainer) {
            destinationInventory = new CraftInventoryDoubleChest((CompoundContainer) destination);
        } else {
            destinationInventory = destination.getOwner().getInventory();
        }

        InventoryMoveItemEvent event = new InventoryMoveItemEvent(source.getOwner().getInventory(), oitemstack,
                destinationInventory, true);
        level.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            // 返回非空 remainder：vanilla 走 else 分支把源格原样写回，等价于 CB 的提前 return
            return stack.copy();
        }
        return HopperBlockEntity.addItem(source, destination, CraftItemStack.asNMSCopy(event.getItem()), direction);
    }
}
