package com.mohistmc.silkard.mixin.vanilla.server.players;

import com.llamalad7.mixinextras.sugar.Local;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(targets = "net.minecraft.server.players.OldUsersConverter$5")
public class MixinOldUsersConverter$5 {

    // CraftBukkit start - use the old file name to seed lastKnownName
    @Inject(method = "movePlayerFile", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/OldUsersConverter;ensureDirectoryExists(Ljava/io/File;)V", shift = At.Shift.BEFORE))
    private void silkard$seedLastKnownName(CallbackInfo ci, @Local(index = 4) File newFile, @Local(index = 5) File oldFile, @Local(index = 3) String oldName) {
        CompoundTag root = null;
        try {
            root = NbtIo.readCompressed(new FileInputStream(oldFile), NbtAccounter.unlimitedHeap());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        if (root != null) {
            if (!root.contains("bukkit")) {
                root.put("bukkit", new CompoundTag());
            }
            CompoundTag data = root.getCompoundOrEmpty("bukkit");
            data.putString("lastKnownName", oldName);
            try {
                NbtIo.writeCompressed(root, new FileOutputStream(newFile));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    // CraftBukkit end
}
