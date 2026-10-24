package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import net.minecraft.world.entity.npc.villager.Villager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(VillagerMakeLove.class)
public class MixinVillagerMakeLove {

    /**
     * @author Mgazul
     * @reason CraftBukkit - EntityBreedEvent
     */
    @Overwrite
    private Optional<Villager> breed(ServerLevel level, Villager source, Villager target) {
        Villager child = source.getBreedOffspring(level, target);
        if (child == null) {
            return Optional.empty();
        }

        // CraftBukkit start - call EntityBreedEvent
        if (CraftEventFactory.callEntityBreedEvent(child, source, target, null, null, 0).isCancelled()) {
            return Optional.empty();
        }
        // Move age setting down
        source.setAge(6000);
        target.setAge(6000);
        child.setAge(-24000);
        child.snapTo(source.getX(), source.getY(), source.getZ(), 0.0F, 0.0F);
        ((com.mohistmc.silkard.injected.world.level.ContextServerLevelAccessor) level).addFreshEntityWithPassengers(child, CreatureSpawnEvent.SpawnReason.BREEDING); // CraftBukkit - added SpawnReason
        // CraftBukkit end
        level.broadcastEntityEvent(child, (byte) 12);
        return Optional.of(child);
    }
}
