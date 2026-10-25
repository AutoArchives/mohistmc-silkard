package com.mohistmc.silkard.mixin.vanilla.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.resources.ResourceKey;
import org.bukkit.craftbukkit.inventory.CraftInventoryFurnace;
import org.bukkit.craftbukkit.inventory.view.CraftFurnaceView;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceMenu.class)
public abstract class MixinAbstractFurnaceMenu extends AbstractContainerMenu {

    @Shadow @Final protected Container container;

    // CraftBukkit start
    @Unique
    private CraftFurnaceView bukkitEntity = null;

    @Unique
    private Inventory player;

    protected MixinAbstractFurnaceMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Override
    public CraftFurnaceView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventoryFurnace inventory = new CraftInventoryFurnace((AbstractFurnaceBlockEntity) this.container);
        bukkitEntity = new CraftFurnaceView(this.player.player.getBukkitEntity(), inventory, (AbstractFurnaceMenu) (Object) this);
        return bukkitEntity;
    }
    // CraftBukkit end

    @Inject(method = "<init>(Lnet/minecraft/world/inventory/MenuType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/inventory/RecipeBookType;ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V", at = @At("RETURN"))
    public void silkard$init(MenuType menuType, ResourceKey allowedInputs, RecipeBookType recipeBookType, int containerId, Inventory inventory, Container container, ContainerData data, CallbackInfo ci) {
        this.player = inventory;
    }

    @Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
    public void silkard$stillValid(net.minecraft.world.entity.player.Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!silkard$checkReachable()) {
            cir.setReturnValue(true);
        }
    }
}
