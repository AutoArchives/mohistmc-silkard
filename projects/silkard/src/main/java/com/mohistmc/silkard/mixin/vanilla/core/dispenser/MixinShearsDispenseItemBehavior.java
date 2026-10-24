package com.mohistmc.silkard.mixin.vanilla.core.dispenser;

import com.mohistmc.silkard.bukkit.DispenseEventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShearsDispenseItemBehavior.class)
public abstract class MixinShearsDispenseItemBehavior extends OptionalDispenseItemBehavior {

    @Shadow
    private static boolean tryShearBeehive(ServerLevel level, ItemStack tool, BlockPos pos) {
        throw new AssertionError();
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockDispenseEvent + BlockShearEntityEvent
     */
    @Overwrite
    protected ItemStack execute(BlockSource source, ItemStack dispensed) {
        ServerLevel serverlevel = source.level();
        if (!serverlevel.isClientSide()) {
            BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));

            // CraftBukkit start
            BlockDispenseEvent event = DispenseEventHooks.callBlockDispenseEvent(serverlevel, source, dispensed, new org.bukkit.util.Vector(0, 0, 0));
            if (event.isCancelled()) {
                return dispensed;
            }

            if (DispenseEventHooks.chainToChangedItem(source, dispensed, event, this)) {
                return dispensed;
            }
            // CraftBukkit end

            this.setSuccess(tryShearBeehive(serverlevel, dispensed, blockpos) || silkard$tryShearEntity(serverlevel, blockpos, dispensed, event)); // CraftBukkit
            if (this.isSuccess()) {
                dispensed.hurtAndBreak(1, serverlevel, null, item -> {
                });
            }
        }

        return dispensed;
    }

    // CraftBukkit start - add event args, fire BlockShearEntityEvent
    @Unique
    private static boolean silkard$tryShearEntity(ServerLevel level, BlockPos pos, ItemStack tool, BlockDispenseEvent event) {
        for (Entity entity : level.getEntitiesOfClass(Entity.class, new AABB(pos), EntitySelector.NO_SPECTATORS)) {
            if (entity.shearOffAllLeashConnections(null)) {
                return true;
            }

            if (entity.isAlive() && entity instanceof Shearable shearable) {
                if (shearable.readyForShearing()) {
                    // CraftBukkit start
                    if (CraftEventFactory.callBlockShearEntityEvent(entity, event.getBlock(), CraftItemStack.asCraftMirror(tool)).isCancelled()) {
                        continue;
                    }
                    // CraftBukkit end
                    shearable.shear(level, SoundSource.BLOCKS, tool);
                    level.gameEvent((Entity) null, GameEvent.SHEAR, pos);
                    return true;
                }
            }
        }

        return false;
    }
    // CraftBukkit end
}
