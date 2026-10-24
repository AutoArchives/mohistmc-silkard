package com.mohistmc.silkard.mixin.vanilla.core.dispenser;

import com.mohistmc.silkard.bukkit.DispenseEventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.block.BlockDispenseEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BoatDispenseItemBehavior.class)
public abstract class MixinBoatDispenseItemBehavior {

    @Shadow
    @Final
    private DefaultDispenseItemBehavior defaultDispenseItemBehavior;

    @Shadow
    @Final
    private EntityType<? extends AbstractBoat> type;

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockDispenseEvent
     */
    @Overwrite
    public ItemStack execute(BlockSource source, ItemStack dispensed) {
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        ServerLevel serverlevel = source.level();
        Vec3 vec3 = source.center();
        double d0 = 0.5625 + this.type.getWidth() / 2.0D;
        double d1 = vec3.x() + direction.getStepX() * d0;
        double d2 = vec3.y() + direction.getStepY() * 1.125F;
        double d3 = vec3.z() + direction.getStepZ() * d0;
        BlockPos blockpos = source.pos().relative(direction);
        double d4;
        if (serverlevel.getFluidState(blockpos).is(FluidTags.WATER)) {
            d4 = 1.0D;
        } else {
            if (!serverlevel.getBlockState(blockpos).isAir() || !serverlevel.getFluidState(blockpos.below()).is(FluidTags.WATER)) {
                return this.defaultDispenseItemBehavior.dispense(source, dispensed);
            }

            d4 = 0.0D;
        }

        // CraftBukkit start
        ItemStack itemstack1 = dispensed.split(1);
        BlockDispenseEvent event = DispenseEventHooks.callBlockDispenseEvent(serverlevel, source, itemstack1, new org.bukkit.util.Vector(d1, d2 + d4, d3));
        if (event.isCancelled()) {
            dispensed.grow(1);
            return dispensed;
        }

        if (DispenseEventHooks.chainToChangedItem(source, itemstack1, event, this)) {
            dispensed.grow(1);
            return dispensed;
        }
        // CraftBukkit end

        AbstractBoat abstractboat = this.type.create(serverlevel, EntitySpawnReason.DISPENSER);
        if (abstractboat != null) {
            abstractboat.setInitialPos(event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ()); // CraftBukkit
            EntityType.<AbstractBoat>createDefaultStackConfig(serverlevel, dispensed, null).apply(abstractboat);
            abstractboat.setYRot(direction.toYRot());
            if (!serverlevel.addFreshEntity(abstractboat)) dispensed.grow(1); // CraftBukkit
            // dispensed.shrink(1); // CraftBukkit - handled during event processing
        }

        return dispensed;
    }
}
