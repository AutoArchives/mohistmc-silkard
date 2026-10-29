package com.mohistmc.silkard.mixin.vanilla.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(TridentItem.class)
public class MixinTridentItem {

    // CraftBukkit start
    @Redirect(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileFromRotation(Lnet/minecraft/world/entity/projectile/Projectile$ProjectileFactory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;FFF)Lnet/minecraft/world/entity/projectile/Projectile;"))
    private Projectile silkard$spawnTrident(Projectile.ProjectileFactory<ThrownTrident> factory, ServerLevel level, ItemStack stack, net.minecraft.world.entity.LivingEntity owner, float x, float y, float z) {
        Projectile projectile = Projectile.spawnProjectileFromRotation(factory, level, stack, owner, x, y, z);
        if (projectile.isRemoved() && owner instanceof ServerPlayer serverPlayer) {
            serverPlayer.getBukkitEntity().updateInventory();
        }
        return projectile;
    }

    @Redirect(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;push(DDD)V"))
    private void silkard$callPlayerRiptideEvent(Player entity, double x, double y, double z, @Local(argsOnly = true) ItemStack stack) {
        CraftEventFactory.callPlayerRiptideEvent(entity, stack, (float) x, (float) y, (float) z);
        entity.push(x, y, z);
    }
    // CraftBukkit end
}
