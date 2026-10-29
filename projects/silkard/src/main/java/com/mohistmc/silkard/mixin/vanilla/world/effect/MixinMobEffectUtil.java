package com.mohistmc.silkard.mixin.vanilla.world.effect;

import com.mohistmc.silkard.injected.world.effect.ContextMobEffectUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(MobEffectUtil.class)
public class MixinMobEffectUtil {

    // CraftBukkit - add Bukkit potion effect cause
    @Redirect(method = "lambda$addEffectToPlayersAround$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean silkard$addEffect(ServerPlayer player, MobEffectInstance effectInstance, Entity entity) {
        return player.addEffect(effectInstance, entity, ContextMobEffectUtil.CAUSE.get() == null ? EntityPotionEffectEvent.Cause.UNKNOWN : ContextMobEffectUtil.CAUSE.get());
    }
}
