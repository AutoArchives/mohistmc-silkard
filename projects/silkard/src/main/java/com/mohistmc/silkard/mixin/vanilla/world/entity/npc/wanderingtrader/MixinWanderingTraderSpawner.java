package com.mohistmc.silkard.mixin.vanilla.world.entity.npc.wanderingtrader;

import com.mohistmc.silkard.injected.world.level.ContextServerLevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PostSpawnProcessor;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(targets = "net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner")
public class MixinWanderingTraderSpawner {

    @Redirect(method = {"spawn", "tryToSpawnLlamaFor"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;spawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntitySpawnReason;)Lnet/minecraft/world/entity/Entity;"))
    private static Entity silkard$spawn(EntityType<?> type, ServerLevel level, BlockPos pos, EntitySpawnReason reason) {
        Entity entity = type.create(level, PostSpawnProcessor.nop(), pos, reason, false, false);
        if (entity != null) {
            ((ContextServerLevelAccessor) level).addFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.NATURAL);
            if (entity instanceof Mob mob) {
                mob.playAmbientSound();
            }
        }
        return entity;
    }
}
