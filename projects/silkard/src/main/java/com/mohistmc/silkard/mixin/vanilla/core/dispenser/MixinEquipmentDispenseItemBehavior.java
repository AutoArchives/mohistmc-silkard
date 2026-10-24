package com.mohistmc.silkard.mixin.vanilla.core.dispenser;

import com.mohistmc.silkard.bukkit.DispenseEventHooks;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.EquipmentDispenseItemBehavior;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EquipmentDispenseItemBehavior.class)
public abstract class MixinEquipmentDispenseItemBehavior {

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockDispenseArmorEvent
     */
    @Overwrite
    public static boolean dispenseEquipment(BlockSource source, ItemStack dispensed) {
        BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
        List<LivingEntity> list = source.level().getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), (livingentity) -> {
            return livingentity.canEquipWithDispenser(dispensed);
        });

        if (list.isEmpty()) {
            return false;
        } else {
            LivingEntity livingentity = list.getFirst();
            EquipmentSlot equipmentslot = livingentity.getEquipmentSlotForItem(dispensed);

            // CraftBukkit start
            ItemStack itemstack1 = dispensed.split(1);
            org.bukkit.block.Block block = CraftBlock.at(source.level(), source.pos());
            BlockDispenseArmorEvent event = new BlockDispenseArmorEvent(block, CraftItemStack.asCraftMirror(itemstack1).clone(), (CraftLivingEntity) livingentity.getBukkitEntity());
            source.level().getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                dispensed.grow(1);
                return false;
            }

            if (!event.getItem().equals(CraftItemStack.asCraftMirror(itemstack1))) {
                dispensed.grow(1);
                // Chain to handler for new item
                ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                DispenseItemBehavior dispenseitembehavior = DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
                if (dispenseitembehavior != DispenseItemBehavior.NOOP && dispenseitembehavior != EquipmentDispenseItemBehavior.INSTANCE) {
                    dispenseitembehavior.dispense(source, eventStack);
                    return true;
                }
            }
            // CraftBukkit end

            livingentity.setItemSlot(equipmentslot, itemstack1);
            if (livingentity instanceof Mob mob) {
                mob.setGuaranteedDrop(equipmentslot);
                mob.setPersistenceRequired();
            }

            return true;
        }
    }
}
