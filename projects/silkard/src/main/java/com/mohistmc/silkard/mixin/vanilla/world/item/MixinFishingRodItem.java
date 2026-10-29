package com.mohistmc.silkard.mixin.vanilla.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.event.player.PlayerFishEvent;
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
@Mixin(FishingRodItem.class)
public class MixinFishingRodItem {

    @Unique
    private boolean silkard$castCancelled = false;

    // CraftBukkit - sound is played after the event
    @Redirect(method = "use", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void silkard$skipThrowSound(Level level, Entity entity, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch) {
    }

    // CraftBukkit - PlayerFishEvent
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectile(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/projectile/Projectile;"))
    private Projectile silkard$spawnProjectile(Projectile projectile, ServerLevel serverLevel, ItemStack stack, @Local(argsOnly = true) Player player, @Local(argsOnly = true) InteractionHand hand) {
        PlayerFishEvent event = new PlayerFishEvent((org.bukkit.entity.Player) player.getBukkitEntity(), null, (org.bukkit.entity.FishHook) projectile.getBukkitEntity(), CraftEquipmentSlot.getHand(hand), PlayerFishEvent.State.FISHING);
        serverLevel.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            this.silkard$castCancelled = true;
            return null;
        }
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (serverLevel.getRandom().nextFloat() * 0.4F + 0.8F));
        return Projectile.spawnProjectile(projectile, serverLevel, stack);
    }

    // CraftBukkit - cancelled casts skip stats and vibration
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/stats/Stat;)V"), cancellable = true)
    private void silkard$cancelCast(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (this.silkard$castCancelled) {
            this.silkard$castCancelled = false;
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
