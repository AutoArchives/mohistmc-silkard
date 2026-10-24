package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

// level / worldPosition 由 BlockEntity 声明（protected），@Shadow 命中不了继承成员 → extends 继承
@Mixin(BaseContainerBlockEntity.class)
public abstract class MixinBaseContainerBlockEntity extends BlockEntity {

    protected MixinBaseContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public org.bukkit.Location getLocation() {
        if (level == null) return null;
        return new org.bukkit.Location(level.getWorld(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
    }
}
