package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(BehaviorUtils.class)
public class MixinBehaviorUtils {

    // CraftBukkit start - SPIGOT-4940: no empty loot
    @Inject(method = "throwItem", at = @At("HEAD"), cancellable = true)
    private static void silkard$noEmptyLoot(LivingEntity thrower, ItemStack item, CallbackInfo ci) {
        if (item.isEmpty()) {
            ci.cancel();
        }
    }

    // CraftBukkit - EntityDropItemEvent
    @Redirect(method = "throwItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean silkard$dropItemEvent(net.minecraft.world.level.Level level, net.minecraft.world.entity.Entity entity, LivingEntity thrower, ItemStack item) {
        org.bukkit.event.entity.EntityDropItemEvent event = new org.bukkit.event.entity.EntityDropItemEvent(thrower.getBukkitEntity(), (org.bukkit.entity.Item) ((ItemEntity) entity).getBukkitEntity());
        level.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        return level.addFreshEntity(entity);
    }
    // CraftBukkit end
}
