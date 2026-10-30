package com.mohistmc.silkard.mixin.vanilla.world.entity.animal;

import com.mohistmc.silkard.injected.world.entity.animal.ContextAnimal;
import com.mohistmc.silkard.injected.world.level.ContextServerLevelAccessor;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(Animal.class)
public class MixinAnimal implements ContextAnimal {

    // CB 自增字段，26.3 vanilla 无此成员
    @Unique
    public ItemStack breedItem;

    @Unique
    private int silkard$breedExperience = -1;
    @Unique
    private boolean silkard$breedCancelled;

    @Override
    public ItemStack getBreedItem() {
        return breedItem;
    }

    // CraftBukkit start - EntityEnterLoveModeEvent
    @Unique
    private int silkard$ticksInLove;

    @Inject(method = "setInLove", at = @At("HEAD"), cancellable = true)
    private void silkard$enterLoveMode(Player player, CallbackInfo ci) {
        EntityEnterLoveModeEvent event = CraftEventFactory.callEntityEnterLoveModeEvent(player, (Animal) (Object) this, 600);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        this.silkard$breedExperience = -1;
        this.silkard$ticksInLove = event.getTicksInLove();
        if (player != null) {
            this.breedItem = player.getInventory().getSelectedItem();
        }
    }

    @Inject(method = "setInLove", at = @At("RETURN"))
    private void silkard$applyTicksInLove(Player player, CallbackInfo ci) {
        ((Animal) (Object) this).inLove = this.silkard$ticksInLove;
    }
    // CraftBukkit end

    // CraftBukkit start - EntityBreedEvent
    @Redirect(method = "spawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;finalizeSpawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;Lnet/minecraft/world/entity/AgeableMob;)V"))
    private void silkard$callBreedEvent(Animal self, ServerLevel level, Animal partner, AgeableMob offspring, ServerLevel levelArg, Animal partnerArg) {
        ServerPlayer breeder = Optional.ofNullable(self.getLoveCause()).or(() -> Optional.ofNullable(partner.getLoveCause())).orElse(null);
        int experience = self.getRandom().nextInt(7) + 1;
        EntityBreedEvent event = CraftEventFactory.callEntityBreedEvent(offspring, self, partner, breeder, ((ContextAnimal) self).getBreedItem(), experience);
        if (event.isCancelled()) {
            this.silkard$breedCancelled = true;
            return;
        }
        this.silkard$breedCancelled = false;
        this.silkard$breedExperience = event.getExperience();
        self.finalizeSpawnChildFromBreeding(level, partner, offspring);
    }

    @Redirect(method = "spawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void silkard$addChild(ServerLevel level, Entity entity, ServerLevel levelArg, Animal partner) {
        if (!this.silkard$breedCancelled) {
            ((ContextServerLevelAccessor) level).addFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.BREEDING);
        }
        this.silkard$breedExperience = -1;
    }

    @Redirect(method = "finalizeSpawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int silkard$breedExperience(RandomSource random, int bound) {
        return this.silkard$breedExperience >= 0 ? this.silkard$breedExperience - 1 : random.nextInt(bound);
    }

    @Redirect(method = "finalizeSpawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$addExperienceOrb(ServerLevel level, Entity entity) {
        return this.silkard$breedExperience != 0 && level.addFreshEntity(entity);
    }
    // CraftBukkit end
}
