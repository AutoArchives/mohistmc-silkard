package com.mohistmc.silkard.mixin.vanilla.server.commands;

import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(SummonCommand.class)
public class MixinSummonCommand {

    @Redirect(method = "createEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tryAddFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean silkard_tryAddFreshEntityWithPassengers(ServerLevel level, Entity entity) {
        return level.tryAddFreshEntityWithPassengers(entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.COMMAND); // CraftBukkit - pass a spawn reason of "COMMAND"
    }
}
