package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetBlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(TransportItemsBetweenContainers.class)
public class MixinTransportItemsBetweenContainers {

    // CraftBukkit start - EntityTargetBlockEvent
    @Inject(method = "getTransportTarget", at = @At("RETURN"), cancellable = true)
    private void silkard$targetBlockEvent(ServerLevel level, PathfinderMob body, CallbackInfoReturnable<Optional<TransportItemsBetweenContainers.TransportItemTarget>> cir) {
        if (cir.getReturnValue().isPresent()) {
            TransportItemsBetweenContainers.TransportItemTarget target = cir.getReturnValue().get();
            EntityTargetBlockEvent event = CraftEventFactory.callEntityTargetBlockEvent(body, level, target.pos());
            if (event.isCancelled()) {
                cir.setReturnValue(Optional.empty());
            }
        }
    }
    // CraftBukkit end
}
