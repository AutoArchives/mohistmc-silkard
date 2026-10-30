package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.wolf;

import com.mohistmc.silkard.injected.world.entity.ContextMob;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(Wolf.class)
public class MixinWolf {

    // CraftBukkit start - fire EntityTameEvent
    @Redirect(method = "tryToTame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/wolf/Wolf;tame(Lnet/minecraft/world/entity/player/Player;)V"))
    private void silkard$tame(Wolf self, Player player) {
        if (CraftEventFactory.callEntityTameEvent((Wolf) (Object) this, player).isCancelled()) {
            return;
        }
        self.tame(player);
    }
    // CraftBukkit end

    // CraftBukkit start - fire EntityTargetEvent with a reason
    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/wolf/Wolf;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void silkard$setTarget(Wolf self, LivingEntity target) {
        ((ContextMob) (Object) this).setTarget(target, EntityTargetEvent.TargetReason.FORGOT_TARGET, true);
    }
    // CraftBukkit end

    // CraftBukkit start - use the scaled max health
    @Redirect(method = "applyTamingSideEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/wolf/Wolf;setHealth(F)V"))
    private void silkard$setHealth(Wolf self, float health) {
        self.setHealth(self.getMaxHealth());
    }
    // CraftBukkit end

    // CraftBukkit start - the wolf only stands up when the damage was applied
    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/wolf/Wolf;setOrderedToSit(Z)V"))
    private void silkard$deferStandUp(Wolf self, boolean sit) {
    }

    @ModifyExpressionValue(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean silkard$standUp(boolean original) {
        if (original) {
            ((TamableAnimal) (Object) this).setOrderedToSit(false);
        }
        return original;
    }
    // CraftBukkit end
}
