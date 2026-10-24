package com.mohistmc.silkard.mixin.vanilla.world.inventory;

import com.mohistmc.silkard.injected.world.inventory.ContextLocationContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftInventoryGrindstone;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
public abstract class MixinGrindstoneMenu extends AbstractContainerMenu {

    @Shadow @Final private Container repairSlots;
    // 26.3 起 ResultContainer 上移为 Container 字段类型
    @Shadow @Final private Container resultSlots;
    @Shadow @Final private ContainerLevelAccess access;

    @Shadow
    private ItemStack computeResult(ItemStack input, ItemStack additional) {
        throw new UnsupportedOperationException();
    }

    // CraftBukkit start
    @Unique
    private CraftInventoryView bukkitEntity = null;

    @Unique
    private org.bukkit.entity.Player player;

    protected MixinGrindstoneMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventoryGrindstone inventory = new CraftInventoryGrindstone(this.repairSlots, this.resultSlots);
        bukkitEntity = new CraftInventoryView(this.player, inventory, (GrindstoneMenu) (Object) this);
        return bukkitEntity;
    }
    // CraftBukkit end

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("RETURN"))
    public void silkard$init(int containerId, Inventory inventory, ContainerLevelAccess access, CallbackInfo ci) {
        this.player = (org.bukkit.entity.Player) inventory.player.getBukkitEntity();
        // CraftBukkit - set location on anonymous containers
        ((ContextLocationContainer) this.repairSlots).silkard$setLocation(access.getLocation());
        ((ContextLocationContainer) (Object) this.resultSlots).silkard$setLocation(access.getLocation());
    }

    @Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
    public void silkard$stillValid(net.minecraft.world.entity.player.Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!silkard$checkReachable()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void silkard$createResult(CallbackInfo ci) {
        org.bukkit.craftbukkit.event.CraftEventFactory.callPrepareGrindstoneEvent(getBukkitView(), this.computeResult(this.repairSlots.getItem(0), this.repairSlots.getItem(1)));
        ((AbstractContainerMenu)(Object)this).sendAllDataToRemote();
        this.broadcastChanges();
        ci.cancel();
    }
}
