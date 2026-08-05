package com.mohistmc.silkard.mixin.vanilla.world.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(FireworkRocketItem.class)
public class MixinFireworkRocketItem {

    @Inject(method = "useOn", at = @At("HEAD"))
    private void silkard_useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        // CraftBukkit
    }
}
