package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.zombie;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import com.mohistmc.silkard.injected.world.entity.ContextMob;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(ZombieVillager.class)
public class MixinZombieVillager {

    @Unique
    private int silkard$lastTick = (int) (System.currentTimeMillis() / 50);

    // CraftBukkit start - use wall time instead of ticks for villager conversion
    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/zombie/ZombieVillager;getConversionProgress()I"))
    private int silkard$wallTime(int original) {
        int elapsedTicks = (int) (System.currentTimeMillis() / 50) - this.silkard$lastTick;
        return original * elapsedTicks;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void silkard$updateLastTick(CallbackInfo ci) {
        this.silkard$lastTick = (int) (System.currentTimeMillis() / 50);
    }
    // CraftBukkit end

    // CraftBukkit start - potion effect causes
    @Redirect(method = "startConverting", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/zombie/ZombieVillager;removeEffect(Lnet/minecraft/core/Holder;)Z"))
    private boolean silkard$removeWeakness(ZombieVillager self, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect) {
        return ((ContextLivingEntity) (Object) self).removeEffect(effect, EntityPotionEffectEvent.Cause.CONVERSION);
    }

    @Redirect(method = "startConverting", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/zombie/ZombieVillager;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean silkard$addStrength(ZombieVillager self, MobEffectInstance effect) {
        return ((ContextLivingEntity) (Object) self).addEffect(effect, EntityPotionEffectEvent.Cause.CONVERSION);
    }
    // CraftBukkit end

    // CraftBukkit start - transform and spawn reasons
    @Redirect(method = "finishConversion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/zombie/ZombieVillager;convertTo(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/ConversionParams;Lnet/minecraft/world/entity/ConversionParams$AfterConversion;)Lnet/minecraft/world/entity/Mob;"))
    private Mob silkard$finishConversion(ZombieVillager self, EntityType<Villager> entityType, ConversionParams params, ConversionParams.AfterConversion<Villager> afterConversion) {
        ((ContextLivingEntity) (Object) self).silkard$forceDrops(false);
        Villager converted = ((ContextMob) (Object) self).convertTo(entityType, params, afterConversion, EntityTransformEvent.TransformReason.CURED, CreatureSpawnEvent.SpawnReason.CURED);
        if (converted == null) {
            ((org.bukkit.entity.ZombieVillager) self.getBukkitEntity()).setConversionTime(-1); // SPIGOT-5208
        }
        return converted;
    }
    // CraftBukkit end

    // CraftBukkit start - nausea potion effect cause
    @Redirect(method = "lambda$finishConversion$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/villager/Villager;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean silkard$addNausea(Villager villager, MobEffectInstance effect, ServerLevel level, Villager villagerArg) {
        return ((ContextLivingEntity) (Object) villager).addEffect(effect, EntityPotionEffectEvent.Cause.CONVERSION);
    }
    // CraftBukkit end
}
