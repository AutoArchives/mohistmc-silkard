package com.mohistmc.silkard.mixin.vanilla.world.food;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(FoodProperties.class)
public class MixinFoodProperties {

    @Redirect(method = "onConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
    private void silkard$eat(FoodData foodData, FoodProperties properties, @Local(argsOnly = true) LivingEntity user, @Local(argsOnly = true) ItemStack stack) {
        foodData.eat(properties, stack, (ServerPlayer) user);
    }
}
