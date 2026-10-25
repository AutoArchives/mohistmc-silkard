package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.spider;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.spider.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Spider.class)
public class MixinSpider {

    @Redirect(method = "finalizeSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/spider/Spider;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean silkard_addEffect(Spider instance, MobEffectInstance mobEffectInstance) {
        return instance.addEffect(mobEffectInstance, org.bukkit.event.entity.EntityPotionEffectEvent.Cause.SPIDER_SPAWN);
    }
}
