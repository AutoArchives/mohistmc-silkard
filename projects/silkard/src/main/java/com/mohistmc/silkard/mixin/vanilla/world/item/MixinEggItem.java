package com.mohistmc.silkard.mixin.vanilla.world.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(EggItem.class)
public class MixinEggItem {

    private static final String SPAWN = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileFromRotation(Lnet/minecraft/world/entity/projectile/Projectile$ProjectileFactory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;FFF)Lnet/minecraft/world/entity/projectile/Projectile;";

    @Unique
    private boolean silkard$removed;

    // CraftBukkit - sound is played after the projectile spawn
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void silkard$skipSound(Level level, Entity entity, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch) {
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = SPAWN))
    private Projectile silkard$spawn(Projectile.ProjectileFactory factory, ServerLevel level, ItemStack stack, LivingEntity source, float yOffset, float pow, float uncertainty) {
        Projectile projectile = Projectile.spawnProjectileFromRotation(factory, level, stack, source, yOffset, pow, uncertainty);
        this.silkard$removed = projectile.isRemoved();
        if (this.silkard$removed && source instanceof ServerPlayer player) {
            player.getBukkitEntity().updateInventory();
        }
        return projectile;
    }

    @Inject(method = "use", cancellable = true, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = SPAWN))
    private void silkard$afterSpawn(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (this.silkard$removed) {
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}
