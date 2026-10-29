package com.mohistmc.silkard.mixin.vanilla.world;

import com.mohistmc.silkard.injected.world.ContextContainer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.Container;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(CompoundContainer.class)
public class MixinCompoundContainer implements ContextContainer {

    @Shadow
    @Final
    public Container container1;

    @Shadow
    @Final
    public Container container2;

    @Unique
    private final List<HumanEntity> silkard$transaction = new ArrayList<>();

    @Override
    public List<ItemStack> getContents() {
        Container self = (Container) (Object) this;
        List<ItemStack> result = new ArrayList<>(self.getContainerSize());
        for (int i = 0; i < self.getContainerSize(); i++) {
            result.add(self.getItem(i));
        }
        return result;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        ((ContextContainer) this.container1).onOpen(who);
        ((ContextContainer) this.container2).onOpen(who);
        this.silkard$transaction.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        ((ContextContainer) this.container1).onClose(who);
        ((ContextContainer) this.container2).onClose(who);
        this.silkard$transaction.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return this.silkard$transaction;
    }

    @Override
    public InventoryHolder getOwner() {
        return null;
    }

    @Override
    public void setMaxStackSize(int size) {
        ((ContextContainer) this.container1).setMaxStackSize(size);
        ((ContextContainer) this.container2).setMaxStackSize(size);
    }

    @Override
    public Location getLocation() {
        return ((ContextContainer) this.container1).getLocation();
    }

    @Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
    private void silkard$maxStackSize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(Math.min(this.container1.getMaxStackSize(), this.container2.getMaxStackSize()));
    }
}
