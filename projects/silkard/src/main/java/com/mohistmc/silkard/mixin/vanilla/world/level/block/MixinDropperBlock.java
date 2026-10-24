package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.core.dispenser.ContextDefaultDispenseItemBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    /**
     * @author Mgazul
     * @reason CraftBukkit - InventoryMoveItemEvent when pushing items into other inventories
     */
    @Overwrite
    public void dispenseFrom(ServerLevel level, BlockState state, BlockPos pos) {
        DispenserBlockEntity dispenserblockentity = level.getBlockEntity(pos, BlockEntityTypes.DROPPER).orElse(null);
        if (dispenserblockentity == null) {
            LOGGER.warn("Ignoring dispensing attempt for Dropper without matching block entity at {}", pos);
        } else {
            BlockSource blocksource = new BlockSource(level, pos, state, dispenserblockentity);
            int i = dispenserblockentity.getRandomSlot(level.getRandom());
            if (i < 0) {
                level.levelEvent(1001, pos, 0);
            } else {
                ItemStack itemstack = dispenserblockentity.getItem(i);
                if (!itemstack.isEmpty()) {
                    Direction direction = level.getBlockState(pos).getValue(DispenserBlock.FACING);
                    Container container = HopperBlockEntity.getContainerAt(level, pos.relative(direction));
                    ItemStack itemstack1;
                    if (container == null) {
                        itemstack1 = DISPENSE_BEHAVIOUR.dispense(blocksource, itemstack);
                    } else {
                        // CraftBukkit start - Fire event when pushing items into other inventories
                        CraftItemStack oitemstack = CraftItemStack.asCraftMirror(itemstack.copyWithCount(1));

                        org.bukkit.inventory.Inventory destinationInventory;
                        // Have to special case large chests as they work oddly
                        if (container instanceof CompoundContainer) {
                            destinationInventory = new CraftInventoryDoubleChest((CompoundContainer) container);
                        } else {
                            destinationInventory = container.getOwner().getInventory();
                        }

                        InventoryMoveItemEvent event = new InventoryMoveItemEvent(dispenserblockentity.getOwner().getInventory(), oitemstack, destinationInventory, true);
                        level.getCraftServer().getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return;
                        }
                        itemstack1 = HopperBlockEntity.addItem(dispenserblockentity, container, CraftItemStack.asNMSCopy(event.getItem()), direction.getOpposite());
                        if (event.getItem().equals(oitemstack) && itemstack1.isEmpty()) {
                            // CraftBukkit end
                            itemstack1 = itemstack.copy();
                            itemstack1.shrink(1);
                        } else {
                            itemstack1 = itemstack.copy();
                        }
                    }

                    dispenserblockentity.setItem(i, itemstack1);
                }
            }
        }
    }
}
