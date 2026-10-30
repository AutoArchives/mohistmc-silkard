package com.mohistmc.silkard.mixin.vanilla.world.entity.vehicle.boat;

import com.mohistmc.silkard.injected.world.ContextContainer;
import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.boat.AbstractChestBoat;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(AbstractChestBoat.class)
public class MixinAbstractChestBoat implements ContextContainer {

    @Shadow
    @Final
    private NonNullList<ItemStack> itemStacks;

    @Unique
    private final List<HumanEntity> silkard$transaction = new ArrayList<>();

    @Unique
    private int silkard$maxStack = MAX_STACK;

    // CraftBukkit start
    public void remove(Entity.RemovalReason reason, EntityRemoveEvent.Cause cause) {
        AbstractChestBoat self = (AbstractChestBoat) (Object) this;
        if (!self.level().isClientSide() && reason.shouldDestroy()) {
            Containers.dropContents(self.level(), self, self);
        }
        ((ContextEntity) self).silkard$superRemove(reason, cause);
    }

    @Override
    public List<ItemStack> getContents() {
        return this.itemStacks;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        this.silkard$transaction.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        this.silkard$transaction.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return this.silkard$transaction;
    }

    @Override
    public InventoryHolder getOwner() {
        org.bukkit.entity.Entity entity = ((ContextEntity) (Object) this).getBukkitEntity();
        return entity instanceof InventoryHolder holder ? holder : null;
    }

    public int getMaxStackSize() {
        return this.silkard$maxStack;
    }

    @Override
    public void setMaxStackSize(int size) {
        this.silkard$maxStack = size;
    }

    @Override
    public Location getLocation() {
        return ((ContextEntity) (Object) this).getBukkitEntity().getLocation();
    }
    // CraftBukkit end
}
