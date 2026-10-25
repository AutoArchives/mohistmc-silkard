package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.InfestedBlock;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InfestedBlock.class)
public class MixinInfestedBlock {

    // 26.3 moved the silverfish spawn into spawnInfestation
    @Redirect(method = "spawnInfestation", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$spawnInfestation(ServerLevel level, Entity entity) {
        return level.addFreshEntity(entity, SpawnReason.SILVERFISH_BLOCK); // CraftBukkit
    }
}
