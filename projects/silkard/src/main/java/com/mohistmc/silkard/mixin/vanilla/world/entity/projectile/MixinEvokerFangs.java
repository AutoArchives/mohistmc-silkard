package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.projectile.EvokerFangs;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(EvokerFangs.class)
public class MixinEvokerFangs {

    // CraftBukkit - add Bukkit remove cause
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/EvokerFangs;discard()V"))
    private void silkard$removeCause(EvokerFangs instance) {
        instance.discard(EntityRemoveEvent.Cause.DESPAWN);
    }

    // CraftBukkit - report the fangs themselves as the damager
    @Redirect(method = "dealDamageTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;magic()Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource silkard$fangDamage(DamageSources sources) {
        return sources.magic().customEntityDamager((EvokerFangs) (Object) this);
    }
}
