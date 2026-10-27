package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile;

import net.minecraft.world.entity.projectile.EyeOfEnder;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(EyeOfEnder.class)
public class MixinEyeOfEnder {

    @Shadow
    private boolean surviveAfterDeath;

    // CraftBukkit - add Bukkit remove cause
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/EyeOfEnder;discard()V"))
    private void silkard$removeCause(EyeOfEnder instance) {
        instance.discard(this.surviveAfterDeath ? EntityRemoveEvent.Cause.DROP : EntityRemoveEvent.Cause.DESPAWN);
    }
}
