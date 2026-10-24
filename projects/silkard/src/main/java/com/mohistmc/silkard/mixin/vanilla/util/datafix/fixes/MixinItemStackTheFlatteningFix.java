package com.mohistmc.silkard.mixin.vanilla.util.datafix.fixes;

import net.minecraft.util.datafix.fixes.ItemStackTheFlatteningFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ItemStackTheFlatteningFix.class)
public class MixinItemStackTheFlatteningFix {

    @Inject(method = "lambda$makeRule$0", at = @At("HEAD"))
    private void silkard_fixItemStack(CallbackInfo ci) {
        // CraftBukkit - TODO 26.3: 仅当 i != 0 时才写 Damage，需按 makeRule 的 OpticFinder 实参重写
    }
}
