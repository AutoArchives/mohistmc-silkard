package com.mohistmc.silkard.mixin.vanilla.server.commands;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import java.util.Set;
import net.minecraft.world.entity.Relative;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(SpreadPlayersCommand.class)
public class MixinSpreadPlayersCommand {

    // CraftBukkit start - handle teleport reason
    @Redirect(method = "setPlayerPositions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FFZ)Z"))
    private static boolean silkard$teleportWithCause(Entity entity, ServerLevel level, double x, double y, double z, Set<Relative> relatives, float yRot, float xRot, boolean resetCamera) {
        return ((ContextEntity) entity).teleportTo(level, x, y, z, relatives, yRot, xRot, resetCamera, PlayerTeleportEvent.TeleportCause.COMMAND);
    }
    // CraftBukkit end
}
