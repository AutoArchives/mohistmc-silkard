package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import com.mohistmc.silkard.injected.world.level.block.entity.ContextBlockEntity;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/4/2 17:16
 */
@Mixin(BlockEntity.class)
public class MixinBlockEntity implements ContextBlockEntity {

    // @formatter:off
    @Shadow protected @Nullable Level level;
    @Shadow @Final protected BlockPos worldPosition;
    @Shadow
    private DataComponentMap components;

    @Shadow
    @Final
    private static org.slf4j.Logger LOGGER;
    // @formatter:on

    // CraftBukkit start - data containers
    private static final CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY = new CraftPersistentDataTypeRegistry();
    public CraftPersistentDataContainer persistentDataContainer;
    // CraftBukkit end

    // CraftBukkit start - read container
    @Inject(method = "loadAdditional", at = @At("RETURN"))
    public void silkard_bukkitRead(ValueInput input, CallbackInfo ci) {
        this.persistentDataContainer = new CraftPersistentDataContainer(DATA_TYPE_REGISTRY);

        input.child("PublicBukkitValues").ifPresent((persistentDataTag) -> {
            this.persistentDataContainer.putAll(persistentDataTag);
        });
    }
    // CraftBukkit end

    @Override
    public CraftPersistentDataContainer silkard_persistentDataContainer() {
        return persistentDataContainer;
    }

    @Inject(method = "saveWithoutMetadata(Lnet/minecraft/world/level/storage/ValueOutput;)V", at = @At("RETURN"))
    private void silkard_bukkitStore(ValueOutput output, CallbackInfo ci) {
        // CraftBukkit start - store container
        if (this.persistentDataContainer != null && !this.persistentDataContainer.isEmpty()) {
            this.persistentDataContainer.store(output.child("PublicBukkitValues"));
        }
        // CraftBukkit end
    }

    @Override
    public InventoryHolder silkard_getOwner() {
        if (this.level == null) return null;
        org.bukkit.block.Block block = CraftBlock.at(this.level, this.worldPosition);
        org.bukkit.block.BlockState state = block.getState();
        if (state instanceof InventoryHolder) return (InventoryHolder) state;
        return null;
    }

    // CraftBukkit - Container.getOwner() for every block entity backed container
    public InventoryHolder getOwner() {
        return this.silkard_getOwner();
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - allow a null chunk base
     */
    @Overwrite
    public static BlockPos getPosFromTag(net.minecraft.world.level.ChunkPos base, net.minecraft.nbt.CompoundTag entityTag) {
        int x = entityTag.getIntOr("x", 0);
        int y = entityTag.getIntOr("y", 0);
        int z = entityTag.getIntOr("z", 0);
        int sectionX = net.minecraft.core.SectionPos.blockToSectionCoord(x);
        int sectionZ = net.minecraft.core.SectionPos.blockToSectionCoord(z);
        if (base != null && (sectionX != base.x() || sectionZ != base.z())) { // CraftBukkit - allow null
            LOGGER.warn("Block entity {} found in a wrong chunk, expected position from chunk {}", entityTag, base);
            x = base.getBlockX(net.minecraft.core.SectionPos.sectionRelative(x));
            z = base.getBlockZ(net.minecraft.core.SectionPos.sectionRelative(z));
        }
        return new BlockPos(x, y, z);
    }

    @Shadow
    protected void applyImplicitComponents(DataComponentGetter components) {
        throw new AssertionError();
    }

    // CraftBukkit start - same as applyComponents, but returns the applied implicit component types
    @Override
    public Set<DataComponentType<?>> applyComponentsSet(DataComponentMap prototype, DataComponentPatch patch) {
        Set<DataComponentType<?>> set = new HashSet<>();
        set.add(DataComponents.BLOCK_ENTITY_DATA);
        set.add(DataComponents.BLOCK_STATE);
        DataComponentMap fullView = PatchedDataComponentMap.fromPatch(prototype, patch);
        this.applyImplicitComponents(new DataComponentGetter() {
            @Override
            public <T> @Nullable T get(DataComponentType<? extends T> type) {
                set.add(type);
                return fullView.get(type);
            }

            @Override
            public <T> T getOrDefault(DataComponentType<? extends T> type, T defaultValue) {
                set.add(type);
                return fullView.getOrDefault(type, defaultValue);
            }
        });
        this.components = patch.forget(set::contains).split().added();
        // never actually added by applyImplicitComponents
        set.remove(DataComponents.BLOCK_ENTITY_DATA);
        set.remove(DataComponents.BLOCK_STATE);
        return set;
    }
    // CraftBukkit end
}
