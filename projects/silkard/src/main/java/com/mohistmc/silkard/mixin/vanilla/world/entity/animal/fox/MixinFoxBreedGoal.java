package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.fox;

import com.mohistmc.silkard.injected.world.entity.animal.ContextAnimal;
import com.mohistmc.silkard.injected.world.level.ContextServerLevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fox.Fox;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(targets = "net.minecraft.world.entity.animal.fox.Fox$FoxBreedGoal")
public class MixinFoxBreedGoal {

    @Shadow @Final protected Animal animal;
    @Shadow protected Animal partner;

    // CraftBukkit start - call EntityBreedEvent
    @Redirect(method = "breed", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void silkard$spawnBaby(ServerLevel level, Entity baby) {
        Animal mother = this.animal;
        Animal father = this.partner;
        ServerPlayer breeder = mother.getLoveCause() != null ? mother.getLoveCause() : father.getLoveCause();
        int experience = mother.getRandom().nextInt(7) + 1;
        EntityBreedEvent event = CraftEventFactory.callEntityBreedEvent((Fox) baby, mother, father, breeder, ((ContextAnimal) (Object) mother).getBreedItem(), experience);
        if (event.isCancelled()) {
            return;
        }
        ((ContextServerLevelAccessor) level).addFreshEntityWithPassengers(baby, CreatureSpawnEvent.SpawnReason.BREEDING);
    }
    // CraftBukkit end
}
