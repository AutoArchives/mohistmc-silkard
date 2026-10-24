package com.mohistmc.silkard.mixin.vanilla.world.effect;

import com.mohistmc.silkard.injected.world.level.ContextLevelWriter;
import net.minecraft.world.effect.OozingMobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(OozingMobEffect.class)
public class MixinOozingMobEffect {

    // 26.3 把溅出史莱姆的逻辑从 applyEffectTick 挪到 spawnSlimeOffspring（owner 已变成 Level）
    @Redirect(method = "spawnSlimeOffspring", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard_addFreshEntity(Level level, Entity entity) {
        return ((ContextLevelWriter) level).addFreshEntity(entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.POTION_EFFECT); // CraftBukkit
    }
}
