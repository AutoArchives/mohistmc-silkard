package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.feline;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.feline.Ocelot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(Ocelot.class)
public class MixinOcelot {

    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int silkard_nextInt(RandomSource random, int bound) {
        int result = random.nextInt(bound);
        // CraftBukkit - added event call and isCancelled check
        return result;
    }
}
