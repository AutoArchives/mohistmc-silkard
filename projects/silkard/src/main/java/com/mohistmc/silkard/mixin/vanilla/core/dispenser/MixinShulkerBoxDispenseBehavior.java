package com.mohistmc.silkard.mixin.vanilla.core.dispenser;

import com.mohistmc.silkard.bukkit.DispenseEventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.DispenserBlock;
import org.bukkit.event.block.BlockDispenseEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShulkerBoxDispenseBehavior.class)
public abstract class MixinShulkerBoxDispenseBehavior extends OptionalDispenseItemBehavior {

    @Shadow
    @Final
    private static Logger LOGGER;

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockDispenseEvent
     */
    @Overwrite
    protected ItemStack execute(BlockSource source, ItemStack dispensed) {
        this.setSuccess(false);
        if (dispensed.getItem() instanceof BlockItem blockitem) {
            Direction direction = source.state().getValue(DispenserBlock.FACING);
            BlockPos blockpos = source.pos().relative(direction);
            Direction direction1 = source.level().isEmptyBlock(blockpos.below()) ? direction : Direction.UP;

            // CraftBukkit start
            BlockDispenseEvent event = DispenseEventHooks.callBlockDispenseEvent(source.level(), source, dispensed, new org.bukkit.util.Vector(blockpos.getX(), blockpos.getY(), blockpos.getZ()));
            if (event.isCancelled()) {
                return dispensed;
            }

            if (DispenseEventHooks.chainToChangedItem(source, dispensed, event, this)) {
                return dispensed;
            }
            // CraftBukkit end

            try {
                this.setSuccess(blockitem.place(new DirectionalPlaceContext(source.level(), blockpos, direction, dispensed, direction1)).consumesAction());
            } catch (Exception exception) {
                LOGGER.error("Error trying to place shulker box at {}", blockpos, exception);
            }
        }

        return dispensed;
    }
}
