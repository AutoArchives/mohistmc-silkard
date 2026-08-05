package com.mohistmc.silkard.mixin.vanilla.world.item.consume_effects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ClearAllStatusEffectsConsumeEffect.class)
public class MixinClearAllStatusEffectsConsumeEffect {

    @Inject(method = "apply", at = @At("HEAD"))
    private void silkard_apply(Level level, ItemStack stack, LivingEntity user, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit
    }
}
