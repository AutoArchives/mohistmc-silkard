package com.mohistmc.silkard.mixin.vanilla.server.commands;

import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(EffectCommands.class)
public class MixinEffectCommands {

    // CraftBukkit start - fire EntityPotionEffectEvent with COMMAND cause
    @Redirect(method = "giveEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean silkard$addEffect(LivingEntity livingentity, MobEffectInstance mobeffectinstance, net.minecraft.world.entity.Entity entity) {
        return ((ContextLivingEntity) livingentity).addEffect(mobeffectinstance, entity, EntityPotionEffectEvent.Cause.COMMAND);
    }

    @Redirect(method = "clearEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeAllEffects()Z"))
    private static boolean silkard$removeAllEffects(LivingEntity livingentity) {
        return ((ContextLivingEntity) livingentity).removeAllEffects(EntityPotionEffectEvent.Cause.COMMAND);
    }

    @Redirect(method = "clearEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeEffect(Lnet/minecraft/core/Holder;)Z"))
    private static boolean silkard$removeEffect(LivingEntity livingentity, Holder<MobEffect> holder) {
        return ((ContextLivingEntity) livingentity).removeEffect(holder, EntityPotionEffectEvent.Cause.COMMAND);
    }
    // CraftBukkit end
}
