package com.mohistmc.silkard.mixin.vanilla.world.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancements.triggers.PlayerHurtEntityTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityDamageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(Interaction.class)
public class MixinInteraction {

    @Unique
    private EntityDamageEvent silkard$damageEvent;

    // CraftBukkit start - non-living damage event
    @Inject(method = "skipAttackInteraction", at = @At("HEAD"), cancellable = true)
    private void silkard$callDamageEvent(Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (source instanceof Player player) {
            DamageSource damageSource = player.damageSources().playerAttack(player);
            this.silkard$damageEvent = CraftEventFactory.callNonLivingEntityDamageEvent((Interaction) (Object) this, damageSource, 1.0F, false);
            if (this.silkard$damageEvent.isCancelled()) {
                cir.setReturnValue(true);
            }
        }
    }

    @WrapOperation(method = "skipAttackInteraction", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/triggers/PlayerHurtEntityTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;FFZ)V"))
    private void silkard$triggerCriteria(PlayerHurtEntityTrigger instance, ServerPlayer serverplayer, Entity entity, DamageSource damageSource, float damage, float originalDamage, boolean flag, Operation<Void> original) {
        original.call(instance, serverplayer, entity, damageSource, (float) this.silkard$damageEvent.getFinalDamage(), originalDamage, flag);
    }
    // CraftBukkit end
}
