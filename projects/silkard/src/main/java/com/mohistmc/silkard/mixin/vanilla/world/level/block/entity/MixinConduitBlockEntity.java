package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mohistmc.silkard.injected.world.damagesource.ContextDamageSource;
import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(ConduitBlockEntity.class)
public class MixinConduitBlockEntity {

    // CraftBukkit start
    @Redirect(method = "applyEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private static boolean silkard$addEffect(net.minecraft.world.entity.player.Player player, MobEffectInstance effect) {
        return ((ContextLivingEntity) player).addEffect(effect, EntityPotionEffectEvent.Cause.CONDUIT);
    }

    @WrapWithCondition(method = "updateAndAttackTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private static boolean silkard$skipAttachSound(ServerLevel level, Entity entity, double x, double y, double z, net.minecraft.sounds.SoundEvent sound, SoundSource source, float volume, float pitch, ServerLevel methodLevel, BlockPos worldPosition, BlockState blockState, ConduitBlockEntity entity2, boolean isActive) {
        return false;
    }

    @WrapOperation(method = "updateAndAttackTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private static boolean silkard$hurtTarget(LivingEntity livingentity, ServerLevel level, DamageSource source, float amount, Operation<Boolean> original, ServerLevel methodLevel, BlockPos worldPosition, BlockState blockState, ConduitBlockEntity entity, boolean isActive) {
        boolean hurt = original.call(livingentity, level, ((ContextDamageSource) source).directBlock(level, worldPosition), amount);
        if (hurt) {
            level.playSound(null, livingentity.getX(), livingentity.getY(), livingentity.getZ(), SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return hurt;
    }
    // CraftBukkit end
}
