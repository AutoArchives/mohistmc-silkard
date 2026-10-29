package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.zombie;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import com.mohistmc.silkard.injected.world.entity.ContextMob;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PigZombieAngerEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(ZombifiedPiglin.class)
public abstract class MixinZombifiedPiglin implements ContextMob {

    // @formatter:off
    @Shadow @Final private static UniformInt FIRST_ANGER_SOUND_DELAY;
    @Shadow @Final private static UniformInt ALERT_INTERVAL;
    @Shadow @Final private static UniformInt PERSISTENT_ANGER_TIME;
    @Shadow private int playFirstAngerSoundIn;
    @Shadow private int ticksUntilNextAlert;
    // @formatter:on

    // CraftBukkit start
    @Redirect(method = "lambda$alertOthers$3", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/zombie/ZombifiedPiglin;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void silkard$alertSetTarget(ZombifiedPiglin instance, LivingEntity target, ZombifiedPiglin outer) {
        ((ContextMob) instance).setTarget(target, EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY, true);
    }

    @Override
    public boolean setTarget(@Nullable LivingEntity target, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        ZombifiedPiglin self = (ZombifiedPiglin) (Object) this;
        if (self.getTarget() == null && target != null) {
            this.playFirstAngerSoundIn = FIRST_ANGER_SOUND_DELAY.sample(self.getRandom());
            this.ticksUntilNextAlert = ALERT_INTERVAL.sample(self.getRandom());
        }
        return ((ContextMob) (Object) this).silkard$superSetTarget(target, reason, fireEvent);
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - PigZombieAngerEvent
     */
    @Overwrite
    public void startPersistentAngerTimer() {
        ZombifiedPiglin self = (ZombifiedPiglin) (Object) this;
        org.bukkit.entity.Entity target = null;
        EntityReference<LivingEntity> targetReference = self.getPersistentAngerTarget();
        if (targetReference != null) {
            LivingEntity targetHandle = targetReference.getEntity(self.level(), LivingEntity.class);
            if (targetHandle != null) {
                target = ((ContextEntity) targetHandle).getBukkitEntity();
            }
        }
        int anger = PERSISTENT_ANGER_TIME.sample(self.getRandom());
        PigZombieAngerEvent event = new PigZombieAngerEvent((org.bukkit.entity.PigZombie) ((ContextEntity) (Object) this).getBukkitEntity(), target, anger);
        self.level().getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            self.setPersistentAngerTarget(null);
            return;
        }
        self.setPersistentAngerEndTime(self.level().getGameTime() + event.getNewAnger());
    }
    // CraftBukkit end
}
