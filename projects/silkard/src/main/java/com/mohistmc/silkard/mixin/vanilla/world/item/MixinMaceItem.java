package com.mohistmc.silkard.mixin.vanilla.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.event.entity.EntityKnockbackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(MaceItem.class)
public class MixinMaceItem {

    // CraftBukkit - EntityKnockbackEvent
    @Redirect(method = "lambda$knockback$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;push(DDD)V"))
    private static void silkard$push(LivingEntity nearby, double x, double y, double z,
                                     @Local(argsOnly = true, ordinal = 0) Entity attacker,
                                     @Local(ordinal = 0) double knockbackPower) {
        Vec3 push = new Vec3(x, y, z);
        Vec3 raw = attacker.getDeltaMovement().add(push);
        EntityKnockbackEvent event = CraftEventFactory.callEntityKnockbackEvent((CraftLivingEntity) nearby.getBukkitEntity(), attacker, EntityKnockbackEvent.KnockbackCause.ENTITY_ATTACK, knockbackPower, raw, x, y, z);

        if (!event.isCancelled()) {
            nearby.push(CraftVector.toNMS(event.getFinalKnockback()));
        }
    }
}
