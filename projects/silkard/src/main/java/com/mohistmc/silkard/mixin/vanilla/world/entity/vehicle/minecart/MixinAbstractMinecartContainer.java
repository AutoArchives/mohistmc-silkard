package com.mohistmc.silkard.mixin.vanilla.world.entity.vehicle.minecart;

import com.mohistmc.silkard.injected.world.ContextContainer;
import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(AbstractMinecartContainer.class)
public class MixinAbstractMinecartContainer implements ContextContainer {

    @Shadow
    private NonNullList<ItemStack> itemStacks;

    @Unique
    private final List<HumanEntity> silkard$transaction = new ArrayList<>();

    @Unique
    private int silkard$maxStack = MAX_STACK;

    // CraftBukkit start - SPIGOT-3513: size follows container
    @Inject(method = "<init>", at = @At("RETURN"))
    private void silkard$resizeItems(EntityType<?> type, Level level, CallbackInfo ci) {
        AbstractMinecartContainer self = (AbstractMinecartContainer) (Object) this;
        if (this.itemStacks.size() != self.getContainerSize()) {
            this.itemStacks = NonNullList.withSize(self.getContainerSize(), ItemStack.EMPTY);
        }
    }

    public void remove(Entity.RemovalReason reason, EntityRemoveEvent.Cause cause) {
        AbstractMinecartContainer self = (AbstractMinecartContainer) (Object) this;
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
        org.bukkit.entity.Entity cart = ((ContextEntity) (Object) this).getBukkitEntity();
        return cart instanceof InventoryHolder holder ? holder : null;
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
