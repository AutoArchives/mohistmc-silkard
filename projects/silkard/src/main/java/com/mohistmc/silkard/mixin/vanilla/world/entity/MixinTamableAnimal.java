package com.mohistmc.silkard.mixin.vanilla.world.entity;

import net.minecraft.world.entity.TamableAnimal;
import org.bukkit.Location;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(TamableAnimal.class)
public class MixinTamableAnimal {

    // CraftBukkit - EntityRegainHealthEvent
    @Redirect(method = "feed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;heal(F)V"))
    private void silkard$heal(TamableAnimal instance, float amount) {
        instance.heal(amount, EntityRegainHealthEvent.RegainReason.EATING);
    }

    // CraftBukkit - EntityTeleportEvent
    @Redirect(method = "maybeTeleportTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;snapTo(DDDFF)V"))
    private void silkard$snapTo(TamableAnimal instance, double x, double y, double z, float yRot, float xRot) {
        EntityTeleportEvent event = CraftEventFactory.callEntityTeleportEvent(instance, x, y, z);

        if (event.isCancelled()) {
            return;
        }
        Location to = event.getTo();
        instance.snapTo(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
    }
}
