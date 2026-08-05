package com.mohistmc.silkard.mixin.vanilla.world.item.alchemy;

import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(PotionContents.class)
public class MixinPotionContents {

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void silkard_init(CallbackInfo ci) {
        // CraftBukkit
    }
}
