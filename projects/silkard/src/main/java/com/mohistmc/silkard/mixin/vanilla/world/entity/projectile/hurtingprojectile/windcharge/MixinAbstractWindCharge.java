package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile.hurtingprojectile.windcharge;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(AbstractWindCharge.class)
public class MixinAbstractWindCharge {

    @Redirect(method = "onHitBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/hurtingprojectile/windcharge/AbstractWindCharge;discard()V"))
    private static void silkard$discardHitBlock(AbstractWindCharge entity) {
        ((ContextEntity) (Object) entity).discard(EntityRemoveEvent.Cause.HIT);
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/hurtingprojectile/windcharge/AbstractWindCharge;discard()V"))
    private static void silkard$discardHit(AbstractWindCharge entity) {
        ((ContextEntity) (Object) entity).discard(EntityRemoveEvent.Cause.HIT);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/hurtingprojectile/windcharge/AbstractWindCharge;discard()V"))
    private static void silkard$discardOutOfWorld(AbstractWindCharge entity) {
        ((ContextEntity) (Object) entity).discard(EntityRemoveEvent.Cause.OUT_OF_WORLD);
    }
}
