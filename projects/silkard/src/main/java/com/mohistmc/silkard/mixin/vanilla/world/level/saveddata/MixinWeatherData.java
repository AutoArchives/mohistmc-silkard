package com.mohistmc.silkard.mixin.vanilla.world.level.saveddata;

import com.mohistmc.silkard.injected.world.level.saveddata.ContextWeatherData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.WeatherData;
import org.bukkit.Bukkit;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(WeatherData.class)
public class MixinWeatherData implements ContextWeatherData {

    @Shadow
    private boolean raining;

    @Shadow
    private boolean thundering;

    // CraftBukkit start
    @Unique
    private ServerLevel silkard$world;

    @Override
    public void silkard$init(ServerLevel world) {
        this.silkard$world = world;
    }

    @Inject(method = "setRaining", at = @At("HEAD"), cancellable = true)
    private void silkard$setRaining(boolean raining, CallbackInfo ci) {
        if (this.raining == raining) {
            ci.cancel();
            return;
        }

        if (this.silkard$world != null) {
            org.bukkit.World world = this.silkard$world.getWorld();
            if (world != null) {
                WeatherChangeEvent weather = new WeatherChangeEvent(world, raining);
                Bukkit.getServer().getPluginManager().callEvent(weather);
                if (weather.isCancelled()) {
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "setThundering", at = @At("HEAD"), cancellable = true)
    private void silkard$setThundering(boolean thundering, CallbackInfo ci) {
        if (this.thundering == thundering) {
            ci.cancel();
            return;
        }

        if (this.silkard$world != null) {
            org.bukkit.World world = this.silkard$world.getWorld();
            if (world != null) {
                ThunderChangeEvent thunder = new ThunderChangeEvent(world, thundering);
                Bukkit.getServer().getPluginManager().callEvent(thunder);
                if (thunder.isCancelled()) {
                    ci.cancel();
                }
            }
        }
    }
    // CraftBukkit end
}
