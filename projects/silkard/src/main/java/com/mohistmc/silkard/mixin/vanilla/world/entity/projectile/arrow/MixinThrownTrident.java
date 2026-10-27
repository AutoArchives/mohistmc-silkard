package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile.arrow;

import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(ThrownTrident.class)
public class MixinThrownTrident {

    // CraftBukkit - add Bukkit remove cause
    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;discard()V"))
    private void silkard$drop(ThrownTrident instance) {
        instance.discard(EntityRemoveEvent.Cause.DROP);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;discard()V"))
    private void silkard$despawn(ThrownTrident instance) {
        instance.discard(EntityRemoveEvent.Cause.DESPAWN);
    }
}
