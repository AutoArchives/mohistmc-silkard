package com.mohistmc.silkard.mixin.vanilla.world.item.consume_effects;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(RemoveStatusEffectsConsumeEffect.class)
public class MixinRemoveStatusEffectsConsumeEffect {

    // CraftBukkit start
    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeEffect(Lnet/minecraft/core/Holder;)Z"))
    private boolean silkard$removeEffect(LivingEntity user, Holder<MobEffect> holder, @Local(argsOnly = true) ItemStack stack) {
        return ((ContextLivingEntity) user).removeEffect(holder, com.mohistmc.silkard.bukkit.BukkitUtils.consumeEffectCause(stack));
    }
    // CraftBukkit end
}
