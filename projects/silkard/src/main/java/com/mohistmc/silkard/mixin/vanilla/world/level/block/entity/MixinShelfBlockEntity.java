package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import com.mohistmc.silkard.injected.world.ContextContainer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(ShelfBlockEntity.class)
public abstract class MixinShelfBlockEntity extends BlockEntity {

    // @formatter:off
    @Shadow @Final private NonNullList<ItemStack> items;
    @Unique public List<HumanEntity> transaction = new ArrayList<>();
    @Unique private int maxStack = ContextContainer.MAX_STACK;
    // @formatter:on

    protected MixinShelfBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // CraftBukkit start
    public List<ItemStack> getContents() {
        return this.items;
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

    public org.bukkit.Location getLocation() {
        if (level == null) return null;
        return new org.bukkit.Location(level.getWorld(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
    }
    // CraftBukkit end
}
