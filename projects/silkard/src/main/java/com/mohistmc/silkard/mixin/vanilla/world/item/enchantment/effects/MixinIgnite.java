package com.mohistmc.silkard.mixin.vanilla.world.item.enchantment.effects;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.Ignite;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(Ignite.class)
public class MixinIgnite {

    // CraftBukkit - call a combust event when somebody hits with a fire enchanted item
    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    private void silkard$combust(Entity entity, float duration, @Local(argsOnly = true) EnchantedItemInUse item) {
        EntityCombustEvent event;
        if (item.owner() != null) {
            event = new EntityCombustByEntityEvent(item.owner().getBukkitEntity(), entity.getBukkitEntity(), duration);
        } else {
            event = new EntityCombustEvent(entity.getBukkitEntity(), duration);
        }

        org.bukkit.Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        entity.igniteForSeconds(event.getDuration());
    }
}
