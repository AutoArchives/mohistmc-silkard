package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.illager;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.illager.Illusioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 26.3 里 Illusioner 的镜像法术（自身隐身）在包私有嵌套类 Illusioner$IllusionerMirrorSpellGoal 内。
 */
@Mixin(targets = "net.minecraft.world.entity.monster.illager.Illusioner$IllusionerMirrorSpellGoal")
public class MixinIllusioner$IllusionerMirrorSpellGoal {

    @Redirect(method = "performSpellCasting", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/illager/Illusioner;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean silkard$addEffect(Illusioner illusioner, MobEffectInstance effect) {
        return illusioner.addEffect(effect, org.bukkit.event.entity.EntityPotionEffectEvent.Cause.ILLUSION); // CraftBukkit
    }
}
