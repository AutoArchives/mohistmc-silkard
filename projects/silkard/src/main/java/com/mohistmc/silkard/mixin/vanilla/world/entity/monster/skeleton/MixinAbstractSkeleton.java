package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.skeleton;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(AbstractSkeleton.class)
public class MixinAbstractSkeleton {

    @Unique
    private static boolean silkard$shootCancelled;

    @Redirect(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileUsingShoot(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;DDDFF)Lnet/minecraft/world/entity/projectile/Projectile;"))
    private Projectile silkard$shoot(Projectile projectile, ServerLevel level, ItemStack stack, double x, double y, double z, float velocity, float spread) {
        EntityShootBowEvent event = CraftEventFactory.callEntityShootBowEvent((LivingEntity) (Object) this, ((LivingEntity) (Object) this).getMainHandItem(), null, projectile, InteractionHand.MAIN_HAND, 0.8F, true);
        if (event.isCancelled()) {
            event.getProjectile().remove();
            silkard$shootCancelled = true;
            return null;
        }

        silkard$shootCancelled = false;
        if (event.getProjectile() == projectile.getBukkitEntity()) {
            return Projectile.spawnProjectileUsingShoot(projectile, level, stack, x, y, z, velocity, spread);
        }
        return null;
    }

    @WrapWithCondition(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/skeleton/AbstractSkeleton;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private boolean silkard$shootSound(AbstractSkeleton self, SoundEvent sound, float volume, float pitch) {
        return !silkard$shootCancelled;
    }

    public void onEquipItem(EquipmentSlot slot, ItemStack oldStack, ItemStack stack, boolean silent) {
        ((LivingEntity) (Object) this).onEquipItem(slot, oldStack, stack);
    }
}
