package com.mohistmc.silkard.mixin.vanilla.world.level;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(BaseSpawner.class)
public class MixinBaseSpawner {

    @Shadow
    private WeightedList<SpawnData> spawnPotentials;

    // CraftBukkit - SPIGOT-3496, MC-92282
    @Inject(method = "setEntityId", at = @At("RETURN"))
    private void silkard$clearPotentials(EntityType<?> type, Level level, RandomSource random, BlockPos pos, CallbackInfo ci) {
        this.spawnPotentials = WeightedList.of();
    }

    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tryAddFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$spawnerSpawn(ServerLevel level, Entity entity, @Local(argsOnly = true) BlockPos pos) {
        if (CraftEventFactory.callSpawnerSpawnEvent(entity, pos).isCancelled()) {
            entity.discard();
            return true;
        }
        return level.tryAddFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.SPAWNER);
    }
}
