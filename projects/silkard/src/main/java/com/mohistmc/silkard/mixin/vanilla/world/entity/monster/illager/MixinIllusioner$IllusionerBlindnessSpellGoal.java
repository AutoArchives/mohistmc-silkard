package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.illager;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 26.3 里 Illusioner 的致盲法术在包私有嵌套类 Illusioner$IllusionerBlindnessSpellGoal 内。
 */
@Mixin(targets = "net.minecraft.world.entity.monster.illager.Illusioner$IllusionerBlindnessSpellGoal")
public class MixinIllusioner$IllusionerBlindnessSpellGoal {

    @Redirect(method = "performSpellCasting", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$addEffectToTarget(LivingEntity target, MobEffectInstance effect, LivingEntity source) {
        return target.addEffect(effect, source, org.bukkit.event.entity.EntityPotionEffectEvent.Cause.ATTACK); // CraftBukkit
    }
}
