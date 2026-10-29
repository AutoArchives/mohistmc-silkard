package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.dolphin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(targets = "net.minecraft.world.entity.animal.dolphin.Dolphin$DolphinSwimWithPlayerGoal")
public class MixinDolphinSwimWithPlayerGoal {

    @Redirect(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$startEffect(Player player, MobEffectInstance effect, Entity source) {
        return player.addEffect(effect, source, EntityPotionEffectEvent.Cause.DOLPHIN);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$tickEffect(Player player, MobEffectInstance effect, Entity source) {
        return player.addEffect(effect, source, EntityPotionEffectEvent.Cause.DOLPHIN);
    }
}
