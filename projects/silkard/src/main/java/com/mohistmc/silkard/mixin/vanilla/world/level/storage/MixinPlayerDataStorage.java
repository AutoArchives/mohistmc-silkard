package com.mohistmc.silkard.mixin.vanilla.world.level.storage;

import com.mohistmc.silkard.injected.world.level.storage.ContextPlayerDataStorage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spigotmc.SpigotConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(PlayerDataStorage.class)
public class MixinPlayerDataStorage implements ContextPlayerDataStorage {

    @Inject(method = "save(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"))
    private void silkard$disablePlayerDataSaving(Player player, CallbackInfo ci) {
        if (org.spigotmc.SpigotConfig.disablePlayerDataSaving) {
            return;
        }
    }
}
