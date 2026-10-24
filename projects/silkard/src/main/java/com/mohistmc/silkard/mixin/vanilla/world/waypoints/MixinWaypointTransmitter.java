package com.mohistmc.silkard.mixin.vanilla.world.waypoints;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(WaypointTransmitter.class)
public interface MixinWaypointTransmitter {

    // CraftBukkit start
    @Inject(method = "doesSourceIgnoreReceiver", at = @At("HEAD"), cancellable = true)
    private static void silkard$canSee(LivingEntity source, ServerPlayer receiver, CallbackInfoReturnable<Boolean> cir) {
        if (!receiver.getBukkitEntity().canSee(source.getBukkitEntity())) {
            cir.setReturnValue(true);
        }
    }
    // CraftBukkit end
}
