package com.mohistmc.silkard.mixin.vanilla.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.InfestedMobEffect;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(InfestedMobEffect.class)
public class MixinInfestedMobEffect {

    // 26.3 把孵化蠹虫的逻辑从 applyEffectTick 挪到 spawnSilverfish
    @Redirect(method = "spawnSilverfish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard_addFreshEntity(ServerLevel level, Entity entity) {
        return level.addFreshEntity(entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.POTION_EFFECT); // CraftBukkit
    }
}
