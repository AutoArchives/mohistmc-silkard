package com.mohistmc.silkard.mixin.vanilla.world.inventory;

import com.mohistmc.silkard.injected.world.ContextContainer;
import com.mohistmc.silkard.injected.world.inventory.ContextLocationContainer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(SimpleContainer.class)
public class MixinSimpleContainer implements ContextLocationContainer {

    // @formatter:off
    @Unique private Location silkard$location;
    @Unique public List<HumanEntity> transaction = new ArrayList<>();
    @Unique private int maxStack = ContextContainer.MAX_STACK;
    @Unique protected InventoryHolder bukkitOwner;
    // @formatter:on

    @Override
    public Location getLocation() {
        return silkard$location;
    }

    @Override
    public void silkard$setLocation(Location location) {
        this.silkard$location = location;
    }

    // CraftBukkit start
    public List<ItemStack> getContents() {
        return ((SimpleContainer) (Object) this).getItems();
    }

    public void onOpen(CraftHumanEntity who) {
        this.transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        this.transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return this.transaction;
    }

    public int getMaxStackSize() {
        return this.maxStack;
    }

    public void setMaxStackSize(int size) {
        this.maxStack = size;
    }

    public InventoryHolder getOwner() {
        return this.bukkitOwner;
    }

    public void setOwner(InventoryHolder owner) {
        this.bukkitOwner = owner;
    }
    // CraftBukkit end
}
