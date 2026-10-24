package com.mohistmc.silkard.mixin.vanilla.core.dispenser;

import com.mohistmc.silkard.bukkit.DispenseEventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.MinecartDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.block.BlockDispenseEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecartDispenseItemBehavior.class)
public abstract class MixinMinecartDispenseItemBehavior {

    @Shadow
    @Final
    private DefaultDispenseItemBehavior defaultDispenseItemBehavior;

    @Shadow
    @Final
    private EntityType<? extends AbstractMinecart> entityType;

    @Shadow
    private static RailShape getRailShape(BlockState state) {
        throw new AssertionError();
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockDispenseEvent
     */
    @Overwrite
    public ItemStack execute(BlockSource source, ItemStack dispensed) {
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        ServerLevel serverlevel = source.level();
        Vec3 vec3 = source.center();
        double d0 = vec3.x() + direction.getStepX() * 1.125D;
        double d1 = Math.floor(vec3.y()) + direction.getStepY();
        double d2 = vec3.z() + direction.getStepZ() * 1.125D;
        BlockPos blockpos = source.pos().relative(direction);
        BlockState blockstate = serverlevel.getBlockState(blockpos);
        double d3;
        if (blockstate.is(BlockTags.RAILS)) {
            if (getRailShape(blockstate).isSlope()) {
                d3 = 0.6D;
            } else {
                d3 = 0.1D;
            }
        } else {
            if (!blockstate.isAir()) {
                return this.defaultDispenseItemBehavior.dispense(source, dispensed);
            }

            BlockState blockstate1 = serverlevel.getBlockState(blockpos.below());
            if (!blockstate1.is(BlockTags.RAILS)) {
                return this.defaultDispenseItemBehavior.dispense(source, dispensed);
            }

            if (direction != Direction.DOWN && getRailShape(blockstate1).isSlope()) {
                d3 = -0.4D;
            } else {
                d3 = -0.9D;
            }
        }

        Vec3 vec31 = new Vec3(d0, d1 + d3, d2);

        // CraftBukkit start
        ItemStack itemstack1 = dispensed.split(1);
        BlockDispenseEvent event = DispenseEventHooks.callBlockDispenseEvent(serverlevel, source, itemstack1, new org.bukkit.util.Vector(vec31.x, vec31.y, vec31.z));
        if (event.isCancelled()) {
            dispensed.grow(1);
            return dispensed;
        }

        if (DispenseEventHooks.chainToChangedItem(source, itemstack1, event, this)) {
            dispensed.grow(1);
            return dispensed;
        }

        AbstractMinecart abstractminecart = AbstractMinecart.createMinecart(serverlevel, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), this.entityType, EntitySpawnReason.DISPENSER, itemstack1, null);
        if (abstractminecart != null) {
            if (!serverlevel.addFreshEntity(abstractminecart)) dispensed.grow(1); // CraftBukkit
            // dispensed.shrink(1); // CraftBukkit - handled during event processing
        }
        // CraftBukkit end

        return dispensed;
    }
}
