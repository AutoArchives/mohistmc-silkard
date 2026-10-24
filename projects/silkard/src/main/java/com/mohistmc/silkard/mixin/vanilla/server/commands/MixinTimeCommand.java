package com.mohistmc.silkard.mixin.vanilla.server.commands;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.world.clock.ServerClockManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(TimeCommand.class)
public class MixinTimeCommand {

    // CraftBukkit start - per-world time
    @ModifyExpressionValue(method = { "suggestTimeMarkers", "queryGameTime", "queryTime", "queryTimelineTicks", "queryTimelineRepetitions", "setTotalTicks", "addTime", "setTimeToTimeMarker", "setPaused" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;clockManager()Lnet/minecraft/world/clock/ServerClockManager;"))
    private static ServerClockManager silkard$perWorldClockManager(ServerClockManager original, @Local(argsOnly = true) CommandSourceStack source) {
        return source.getLevel().clockManager();
    }
    // CraftBukkit end
}
