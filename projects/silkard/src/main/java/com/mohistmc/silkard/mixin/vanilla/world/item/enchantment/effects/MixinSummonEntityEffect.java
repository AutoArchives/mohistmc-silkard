package com.mohistmc.silkard.mixin.vanilla.world.item.enchantment.effects;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.injected.server.level.ContextServerLevel;
import com.mohistmc.silkard.injected.world.level.ContextServerLevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.SummonEntityEffect;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(SummonEntityEffect.class)
public class MixinSummonEntityEffect {

    // CraftBukkit start
    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;spawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntitySpawnReason;)Lnet/minecraft/world/entity/Entity;"))
    private Entity silkard$create(EntityType<?> type, ServerLevel level, BlockPos pos, EntitySpawnReason reason, @Local(argsOnly = true) EnchantedItemInUse item) {
        Entity entity = type.create(level, null, pos, reason, false, false);
        if (entity == null) {
            return null;
        }
        if (entity instanceof LightningBolt) {
            LightningStrikeEvent.Cause cause = item.itemStack().getItem() == Items.TRIDENT ? LightningStrikeEvent.Cause.TRIDENT : LightningStrikeEvent.Cause.ENCHANTMENT;
            ((ContextServerLevel) level).strikeLightning(entity, cause);
        } else {
            ((ContextServerLevelAccessor) level).addFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.ENCHANTMENT);
        }
        return entity;
    }
    // CraftBukkit end
}
