package com.mohistmc.silkard.mixin.vanilla.util.datafix.fixes;

import com.mojang.datafixers.Typed;
import net.minecraft.util.datafix.fixes.ItemStackMapIdFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ItemStackMapIdFix.class)
public class MixinItemStackMapIdFix {

    @Inject(method = "lambda$makeRule$0", at = @At("HEAD"))
    private static void silkard_fixItemStack(CallbackInfoReturnable<Typed<?>> cir) {
        // CraftBukkit - TODO 26.3: 仅在 map 不存在时才写入，需按 makeRule 的 OpticFinder 实参重写
    }
}
