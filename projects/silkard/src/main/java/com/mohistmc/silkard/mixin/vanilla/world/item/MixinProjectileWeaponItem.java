package com.mohistmc.silkard.mixin.vanilla.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(ProjectileWeaponItem.class)
public class MixinProjectileWeaponItem {

    @Unique
    private boolean silkard$shootCancelled;

    @Inject(method = "shoot", at = @At("HEAD"))
    private void silkard$resetShootState(CallbackInfo ci) {
        this.silkard$shootCancelled = false;
    }

    // CraftBukkit start - EntityShootBowEvent
    @Redirect(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectile(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;)Lnet/minecraft/world/entity/projectile/Projectile;"))
    private Projectile silkard$callEntityShootBowEvent(Projectile projectile, ServerLevel level, ItemStack projectileStack, Consumer<Projectile> shootAction, @Local(argsOnly = true) LivingEntity shooter, @Local(argsOnly = true) InteractionHand hand, @Local(argsOnly = true) ItemStack weapon, @Local(argsOnly = true) float power) {
        if (this.silkard$shootCancelled) {
            return projectile;
        }
        shootAction.accept(projectile);

        EntityShootBowEvent event = CraftEventFactory.callEntityShootBowEvent(shooter, weapon, projectileStack, projectile, hand, power, true);
        if (event.isCancelled()) {
            event.getProjectile().remove();
            this.silkard$shootCancelled = true;
            return projectile;
        }

        if (event.getProjectile() == projectile.getBukkitEntity()) {
            Projectile spawned = Projectile.spawnProjectile(projectile, level, projectileStack);
            if (spawned.isRemoved()) {
                if (shooter instanceof ServerPlayer serverPlayer) {
                    serverPlayer.getBukkitEntity().updateInventory();
                }
                this.silkard$shootCancelled = true;
            }
            return spawned;
        }
        return projectile;
    }

    @Redirect(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V"))
    private void silkard$hurtAndBreak(ItemStack weapon, int amount, LivingEntity entity, EquipmentSlot slot) {
        if (!this.silkard$shootCancelled) {
            weapon.hurtAndBreak(amount, entity, slot);
        }
    }
    // CraftBukkit end
}
