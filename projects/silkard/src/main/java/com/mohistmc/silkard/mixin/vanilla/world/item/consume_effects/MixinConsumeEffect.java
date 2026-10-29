package com.mohistmc.silkard.mixin.vanilla.world.item.consume_effects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(ConsumeEffect.class)
public interface MixinConsumeEffect {

    // CraftBukkit start
    default boolean apply(Level level, ItemStack stack, LivingEntity user, EntityPotionEffectEvent.Cause cause) {
        return ((ConsumeEffect) this).apply(level, stack, user);
    }

    static EntityPotionEffectEvent.Cause silkard$consumeCause(ItemStack stack) {
        if (stack.is(Items.MILK_BUCKET)) {
            return EntityPotionEffectEvent.Cause.MILK;
        } else if (stack.is(Items.POTION)) {
            return EntityPotionEffectEvent.Cause.POTION_DRINK;
        }
        return EntityPotionEffectEvent.Cause.FOOD;
    }
    // CraftBukkit end
}
