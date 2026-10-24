package com.mohistmc.silkard.mixin.vanilla.world.inventory;

import com.mohistmc.silkard.injected.world.inventory.ContextTransientCraftingContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractCraftingMenu.class)
public abstract class MixinAbstractCraftingMenu {

    // 26.3 起 CraftingContainer 由 class 变为 interface，字段类型随之变化
    @Shadow @Final public CraftingContainer craftSlots;
    @Shadow @Final public ResultContainer resultSlots;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void silkard$init(MenuType<?> menuType, int containerId, int width, int height, Inventory playerInventory, CallbackInfo ci) {
        ((ContextTransientCraftingContainer) this.craftSlots).silkard$setResultInventory(this.resultSlots);
    }
}
