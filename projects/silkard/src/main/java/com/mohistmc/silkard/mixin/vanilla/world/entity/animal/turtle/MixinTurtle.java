package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.turtle;

import com.mohistmc.silkard.injected.world.damagesource.ContextDamageSource;
import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.turtle.Turtle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(Turtle.class)
public class MixinTurtle {

    @Inject(method = "ageBoundaryReached", at = @At("HEAD"))
    private void silkard$forceDropsOn(CallbackInfo ci) {
        ((ContextLivingEntity) (Object) this).silkard$forceDrops(true);
    }

    @Inject(method = "ageBoundaryReached", at = @At("RETURN"))
    private void silkard$forceDropsOff(CallbackInfo ci) {
        ((ContextLivingEntity) (Object) this).silkard$forceDrops(false);
    }

    @Redirect(method = "thunderHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;lightningBolt()Lnet/minecraft/world/damagesource/DamageSource;"))
    private static DamageSource silkard$lightning(DamageSources sources, ServerLevel level, LightningBolt lightningBolt) {
        return ((ContextDamageSource) (Object) sources.lightningBolt()).customEntityDamager(lightningBolt);
    }
}
