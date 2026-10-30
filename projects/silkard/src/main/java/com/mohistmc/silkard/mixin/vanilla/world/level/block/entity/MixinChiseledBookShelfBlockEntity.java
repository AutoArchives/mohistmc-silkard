package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mohistmc.silkard.injected.world.ContextContainer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(ChiseledBookShelfBlockEntity.class)
public class MixinChiseledBookShelfBlockEntity implements ContextContainer {

    @Shadow
    @Final
    private NonNullList<ItemStack> items;

    @Unique
    private final List<HumanEntity> silkard$transaction = new ArrayList<>();

    @Unique
    private int silkard$maxStack = 1;

    // CraftBukkit start
    @Override
    public List<ItemStack> getContents() {
        return this.items;
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
    public void setMaxStackSize(int size) {
        this.silkard$maxStack = size;
    }

    @Override
    public Location getLocation() {
        ChiseledBookShelfBlockEntity self = (ChiseledBookShelfBlockEntity) (Object) this;
        if (self.getLevel() == null) {
            return null;
        }
        return new Location(self.getLevel().getWorld(), self.getBlockPos().getX(), self.getBlockPos().getY(), self.getBlockPos().getZ());
    }

    @Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
    private void silkard$maxStack(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.silkard$maxStack);
    }

    // SPIGOT-7381: check for null world
    @WrapWithCondition(method = "removeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/ChiseledBookShelfBlockEntity;updateState(I)V"))
    private boolean silkard$updateStateOnRemove(ChiseledBookShelfBlockEntity instance, int slot, int amount, int maxStackSize) {
        return instance.getLevel() != null;
    }

    @WrapWithCondition(method = "setItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/ChiseledBookShelfBlockEntity;updateState(I)V"))
    private boolean silkard$updateStateOnSet(ChiseledBookShelfBlockEntity instance, int slot) {
        return instance.getLevel() != null;
    }
    // CraftBukkit end
}
