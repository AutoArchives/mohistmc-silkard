package com.mohistmc.silkard.injected.world.entity;

import com.mohistmc.silkard.util.ContextStateException;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.jspecify.annotations.Nullable;

public interface ContextMob extends ContextNeutralMob {

    default boolean getsilkard$targetSuccess() {
        throw new ContextStateException("Not implemented");
    }

    default boolean silkard_aware() {
        throw new ContextStateException("Not implemented");
    }

    default void silkard_aware(boolean aware){
        throw new ContextStateException("Not implemented");
    }

    default void setPersistenceRequired(boolean persistenceRequired) {
        throw new ContextStateException("Not implemented");
    }

    @Override
    default boolean setTarget(LivingEntity entityliving, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        throw new ContextStateException("Not implemented");
    }

    default boolean silkard$superSetTarget(LivingEntity entityliving, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        throw new ContextStateException("Not implemented");
    }

    default SoundEvent getAmbientSound0() {
        throw new ContextStateException("Not implemented");
    }

    default ItemStack equipItemIfPossible(ItemStack itemstack, ItemEntity entityitem) {
        throw new ContextStateException("Not implemented");
    }

    default <T extends Mob> T convertTo(EntityType<T> entitytypes, boolean flag, EntityTransformEvent.TransformReason transformReason, CreatureSpawnEvent.SpawnReason spawnReason) {
        throw new ContextStateException("Not implemented");
    }

    default void silkard$pushGoalTargetReason(EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        throw new ContextStateException("Not implemented");
    }

    default void silkard$pushTransformReason(EntityTransformEvent.TransformReason transformReason) {
        throw new ContextStateException("Not implemented");
    }

    default <T extends Mob> @Nullable T convertTo(EntityType<T> entityType, ConversionParams params, EntitySpawnReason spawnReason, ConversionParams.AfterConversion<T> afterConversion, EntityTransformEvent.TransformReason transformReason, CreatureSpawnEvent.SpawnReason bukkitSpawnReason) {
        throw new ContextStateException("Not implemented");
    }

    default <T extends Mob> @Nullable T convertTo(EntityType<T> entityType, ConversionParams params, ConversionParams.AfterConversion<T> afterConversion, EntityTransformEvent.TransformReason transformReason, CreatureSpawnEvent.SpawnReason spawnReason) {
        throw new ContextStateException("Not implemented");
    }
}
