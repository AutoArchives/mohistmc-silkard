package com.mohistmc.silkard.mixin.vanilla.world.item.consume_effects;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(ApplyStatusEffectsConsumeEffect.class)
public class MixinApplyStatusEffectsConsumeEffect {

    // CraftBukkit start
    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean silkard$addEffect(LivingEntity user, MobEffectInstance instance, @Local(argsOnly = true) ItemStack stack) {
        return ((ContextLivingEntity) user).addEffect(instance, com.mohistmc.silkard.bukkit.BukkitUtils.consumeEffectCause(stack));
    }
    // CraftBukkit end
}
