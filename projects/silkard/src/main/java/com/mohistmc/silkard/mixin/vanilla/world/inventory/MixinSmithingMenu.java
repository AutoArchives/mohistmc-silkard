package com.mohistmc.silkard.mixin.vanilla.world.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import org.bukkit.craftbukkit.inventory.CraftInventorySmithing;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 直接继承 ItemCombinerMenu：inputSlots/resultSlots/access/player 都是它声明的
// protected 字段，@Shadow 无法命中继承成员，只能靠 extends 拿到。
@Mixin(SmithingMenu.class)
public abstract class MixinSmithingMenu extends ItemCombinerMenu {

    // CraftBukkit start
    @Unique
    private CraftInventoryView bukkitEntity;

    protected MixinSmithingMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory,
                                ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(menuType, containerId, playerInventory, access, slotDefinition);
    }

    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventorySmithing inventory = new CraftInventorySmithing(
                access.getLocation(), this.inputSlots, this.resultSlots);
        bukkitEntity = new CraftInventoryView(this.player.getBukkitEntity(), inventory, (SmithingMenu) (Object) this);
        return bukkitEntity;
    }
    // CraftBukkit end

    // 26.3 的 stillValid 只在 ItemCombinerMenu 声明，已由 MixinItemCombinerMenu 覆盖

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void silkard$createResult(CallbackInfo ci) {
        org.bukkit.craftbukkit.event.CraftEventFactory.callPrepareSmithingEvent(getBukkitView(), this.resultSlots.getItem(0));
        this.sendAllDataToRemote();
        this.broadcastChanges();
        ci.cancel();
    }
}
