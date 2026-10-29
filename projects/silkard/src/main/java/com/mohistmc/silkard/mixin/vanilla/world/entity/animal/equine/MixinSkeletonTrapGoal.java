package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.equine;

import com.mohistmc.silkard.injected.server.level.ContextServerLevel;
import com.mohistmc.silkard.injected.world.level.ContextServerLevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.SkeletonTrapGoal;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(SkeletonTrapGoal.class)
public class MixinSkeletonTrapGoal {

    // CraftBukkit start - add causes
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$strikeLightning(ServerLevel instance, Entity entity) {
        return ((ContextServerLevel) instance).strikeLightning(entity, LightningStrikeEvent.Cause.TRAP);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void silkard$addTrapEntity(ServerLevel instance, Entity entity) {
        ((ContextServerLevelAccessor) (Object) instance).addFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.TRAP);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void silkard$addJockeyEntity(ServerLevel instance, Entity entity) {
        ((ContextServerLevelAccessor) (Object) instance).addFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.JOCKEY);
    }
    // CraftBukkit end
}
