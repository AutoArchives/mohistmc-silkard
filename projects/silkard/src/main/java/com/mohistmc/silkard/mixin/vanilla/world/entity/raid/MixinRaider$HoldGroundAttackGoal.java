package com.mohistmc.silkard.mixin.vanilla.world.entity.raid;

import com.mohistmc.silkard.injected.world.entity.ContextMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raider;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(targets = "net.minecraft.world.entity.raid.Raider$HoldGroundAttackGoal")
public class MixinRaider$HoldGroundAttackGoal {

    // CraftBukkit start - fire EntityTargetEvent with FOLLOW_LEADER reason
    @Redirect(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raider;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void silkard$setTargetOnStart(Raider raider, LivingEntity target) {
        ((ContextMob) (Object) raider).setTarget(target, EntityTargetEvent.TargetReason.FOLLOW_LEADER, true);
    }

    @Redirect(method = "stop", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raider;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void silkard$setTargetOnStop(Raider raider, LivingEntity target) {
        ((ContextMob) (Object) raider).setTarget(target, EntityTargetEvent.TargetReason.FOLLOW_LEADER, true);
    }
    // CraftBukkit end
}
