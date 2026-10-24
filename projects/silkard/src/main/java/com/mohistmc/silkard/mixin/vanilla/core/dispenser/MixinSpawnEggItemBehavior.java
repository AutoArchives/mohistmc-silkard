package com.mohistmc.silkard.mixin.vanilla.core.dispenser;

import com.mohistmc.silkard.bukkit.DispenseEventHooks;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.SpawnEggItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SpawnEggItemBehavior.class)
public abstract class MixinSpawnEggItemBehavior extends DefaultDispenseItemBehavior {

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockDispenseEvent
     */
    @Overwrite
    public ItemStack execute(BlockSource source, ItemStack dispensed) {
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        EntityType<?> entitytype = SpawnEggItem.getType(dispensed);
        if (entitytype != null) {
            // CraftBukkit start
            ServerLevel serverlevel = source.level();
            ItemStack itemstack1 = dispensed.split(1);
            BlockDispenseEvent event = DispenseEventHooks.callBlockDispenseEvent(serverlevel, source, itemstack1, new org.bukkit.util.Vector(0, 0, 0));
            if (event.isCancelled()) {
                dispensed.grow(1);
                return dispensed;
            }

            if (DispenseEventHooks.chainToChangedItem(source, itemstack1, event, this)) {
                dispensed.grow(1);
                return dispensed;
            }
            // CraftBukkit end

            try {
                Entity entity = entitytype.spawn(source.level(), dispensed, null, source.pos().relative(direction), EntitySpawnReason.DISPENSER, direction != Direction.UP, false);

                if (entity == null) {
                    return dispensed;
                }
            } catch (Exception exception) {
                DispenseItemBehavior.LOGGER.error("Error while dispensing spawn egg from dispenser at {}", source.pos(), exception);
                return ItemStack.EMPTY;
            }

            // dispensed.shrink(1); // CraftBukkit - Handled during event processing
            source.level().gameEvent(null, GameEvent.ENTITY_PLACE, source.pos());
            return dispensed;
        } else {
            return dispensed;
        }
    }
}
