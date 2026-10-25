package com.mohistmc.silkard.mixin.vanilla.server.level;

import java.util.concurrent.CompletableFuture;
import net.minecraft.server.level.PlayerSpawnFinder;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(PlayerSpawnFinder.class)
public class MixinPlayerSpawnFinder {

    @Inject(method = "findSpawn", at = @At("HEAD"))
    private static void silkard_findSpawn(CallbackInfoReturnable<CompletableFuture<Vec3>> cir) {
        // CraftBukkit
    }
}
