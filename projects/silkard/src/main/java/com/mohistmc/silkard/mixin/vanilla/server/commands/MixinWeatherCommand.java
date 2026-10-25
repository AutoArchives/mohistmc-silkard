package com.mohistmc.silkard.mixin.vanilla.server.commands;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.WeatherCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(WeatherCommand.class)
public class MixinWeatherCommand {

    // CraftBukkit start - SPIGOT-7680: per-world weather
    @Redirect(method = { "setClear", "setRain", "setThunder" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setWeatherParameters(IIZZ)V"))
    private static void silkard$perWorldWeather(MinecraftServer server, int clearTime, int rainTime, boolean raining, boolean thundering, @Local(argsOnly = true) CommandSourceStack source) {
        ((com.mohistmc.silkard.injected.server.level.ContextServerLevel) source.getLevel()).setWeatherParameters(clearTime, rainTime, raining, thundering);
    }
    // CraftBukkit end
}
