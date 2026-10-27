package com.mohistmc.silkard.mixin.vanilla.world.effect;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(targets = "net.minecraft.world.effect.SaturationMobEffect")
public class MixinSaturationMobEffect {

    @Redirect(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private void silkard$eat(FoodData foodData, int amount, float saturation, @Local(argsOnly = true) LivingEntity mob) {
        Player player = (Player) mob;
        int oldFoodLevel = foodData.foodLevel;
        FoodLevelChangeEvent event = CraftEventFactory.callFoodLevelChangeEvent(player, amount + oldFoodLevel);
        if (!event.isCancelled()) {
            foodData.eat(event.getFoodLevel() - oldFoodLevel, saturation);
        }
        ((CraftPlayer) player.getBukkitEntity()).sendHealthUpdate();
    }
}
