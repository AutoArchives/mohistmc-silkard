package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.rabbit;

import net.minecraft.world.entity.animal.rabbit.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(Rabbit.class)
public class MixinRabbit {

    // CraftBukkit start - drop speed modifier reset in constructor
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/rabbit/Rabbit;setSpeedModifier(D)V"))
    private void silkard$skipSpeedModifier(Rabbit instance, double speed) {
    }
    // CraftBukkit end
}
