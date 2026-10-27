package com.mohistmc.silkard.mixin.vanilla.world.level.storage;

import com.mohistmc.silkard.injected.world.entity.player.ContextPlayer;
import com.mohistmc.silkard.injected.world.level.storage.ContextPlayerDataStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.spigotmc.SpigotConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.Optional;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(PlayerDataStorage.class)
public class MixinPlayerDataStorage implements ContextPlayerDataStorage {

    @Shadow @Final private File playerDir;

    @Inject(method = "save(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"))
    private void silkard$disablePlayerDataSaving(Player player, CallbackInfo ci) {
        if (SpigotConfig.disablePlayerDataSaving) {
            ci.cancel();
        }
    }

    // CraftBukkit start
    @Override
    public File getPlayerDir() {
        return this.playerDir;
    }

    @Override
    public Optional<CompoundTag> load(Player player) {
        return ((PlayerDataStorage) (Object) this).load(player.nameAndId()).map((compoundtag) -> {
            if (player instanceof ServerPlayer) {
                CraftPlayer craftPlayer = (CraftPlayer) ((ContextPlayer) player).getBukkitEntity();
                // Only update first played if it is older than the one we have
                long modified = new File(this.playerDir, player.getStringUUID() + ".dat").lastModified();
                if (modified < craftPlayer.getFirstPlayed()) {
                    craftPlayer.setFirstPlayed(modified);
                }
            }

            return compoundtag;
        });
    }
    // CraftBukkit end
}
