package com.mohistmc.silkard.mixin.vanilla.world.effect;

import net.minecraft.world.entity.LivingEntity;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(targets = "net.minecraft.world.effect.HealOrHarmMobEffect")
public class MixinHealOrHarmMobEffect {

    @Redirect(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"))
    private void silkard$healTick(LivingEntity mob, float amount) {
        mob.heal(amount, EntityRegainHealthEvent.RegainReason.MAGIC); // CraftBukkit
    }

    @Redirect(method = "applyInstantaneousEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"))
    private void silkard$healInstant(LivingEntity mob, float amount) {
        mob.heal(amount, EntityRegainHealthEvent.RegainReason.MAGIC); // CraftBukkit
    }
}
