package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.fish;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.entity.player.Player;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(Pufferfish.class)
public class MixinPufferfish {

    @Redirect(method = "touch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$touchEffect(Mob mob, MobEffectInstance effect, Entity source) {
        return mob.addEffect(effect, source, EntityPotionEffectEvent.Cause.ATTACK); // CraftBukkit
    }

    @Redirect(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$playerTouchEffect(Player player, MobEffectInstance effect, Entity source) {
        return player.addEffect(effect, source, EntityPotionEffectEvent.Cause.ATTACK); // CraftBukkit
    }
}
