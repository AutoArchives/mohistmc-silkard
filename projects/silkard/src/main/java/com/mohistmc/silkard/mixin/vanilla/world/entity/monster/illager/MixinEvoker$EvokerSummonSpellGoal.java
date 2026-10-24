package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.illager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 26.3 里 Evoker 的招唤法术逻辑在包私有嵌套类 Evoker$EvokerSummonSpellGoal 内，performSpellCasting 已不在 Evoker 本身。
 */
@Mixin(targets = "net.minecraft.world.entity.monster.illager.Evoker$EvokerSummonSpellGoal")
public class MixinEvoker$EvokerSummonSpellGoal {

    @Redirect(method = "performSpellCasting", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void silkard$addFreshEntityWithPassengers(ServerLevel level, Entity entity) {
        level.addFreshEntityWithPassengers(entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPELL); // CraftBukkit - Add SpawnReason
    }
}
