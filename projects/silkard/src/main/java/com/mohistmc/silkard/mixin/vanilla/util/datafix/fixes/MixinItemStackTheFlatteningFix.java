package com.mohistmc.silkard.mixin.vanilla.util.datafix.fixes;

import com.mojang.datafixers.Typed;
import net.minecraft.util.datafix.fixes.ItemStackTheFlatteningFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ItemStackTheFlatteningFix.class)
public class MixinItemStackTheFlatteningFix {

    @Inject(method = "lambda$makeRule$0", at = @At("HEAD"))
    private static void silkard_fixItemStack(CallbackInfoReturnable<Typed<?>> cir) {
        // CraftBukkit - TODO 26.3: i != 0 才写 damage 的分支需按新实参重写
    }
}
