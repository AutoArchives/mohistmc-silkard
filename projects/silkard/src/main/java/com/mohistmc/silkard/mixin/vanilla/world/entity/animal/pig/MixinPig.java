package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.pig;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import com.mohistmc.silkard.injected.world.entity.ContextMob;
import com.mohistmc.silkard.injected.world.level.ContextLevelWriter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(Pig.class)
public class MixinPig {

    @Unique
    private boolean silkard$zapCancelled;

    // CraftBukkit start - PigZapEvent
    @Redirect(method = "thunderHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/pig/Pig;convertTo(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/ConversionParams;Lnet/minecraft/world/entity/ConversionParams$AfterConversion;)Lnet/minecraft/world/entity/Mob;"))
    private Mob silkard$convert(Pig instance, EntityType<ZombifiedPiglin> entityType, ConversionParams params, ConversionParams.AfterConversion<ZombifiedPiglin> afterConversion, ServerLevel level, LightningBolt lightningBolt) {
        this.silkard$zapCancelled = false;
        ZombifiedPiglin converted = ((ContextMob) instance).convertTo(entityType, params, afterConversion, null, null);
        if (converted == null) {
            return null;
        }
        if (CraftEventFactory.callPigZapEvent(instance, lightningBolt, converted).isCancelled()) {
            this.silkard$zapCancelled = true;
            return null;
        }
        ((ContextLevelWriter) level).addFreshEntity(converted, CreatureSpawnEvent.SpawnReason.LIGHTNING);
        ((ContextEntity) instance).discard(EntityRemoveEvent.Cause.TRANSFORMATION);
        return converted;
    }

    @WrapWithCondition(method = "thunderHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;thunderHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LightningBolt;)V"))
    private boolean silkard$skipSuperThunderHit(Animal instance, ServerLevel level, LightningBolt lightningBolt, ServerLevel methodLevel, LightningBolt methodBolt) {
        return !this.silkard$zapCancelled;
    }
    // CraftBukkit end
}
