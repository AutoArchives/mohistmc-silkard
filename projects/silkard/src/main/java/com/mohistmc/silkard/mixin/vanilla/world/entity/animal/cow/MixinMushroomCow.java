package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.cow;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(MushroomCow.class)
public class MixinMushroomCow {

    // CraftBukkit start - fire PlayerShearEntityEvent
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void silkard$shearEvent(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!CraftEventFactory.handlePlayerShearEntityEvent(player, (MushroomCow) (Object) this, itemstack, hand)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
    // CraftBukkit end

    // CraftBukkit start - fire EntityDropItemEvent for sheared drops
    @Redirect(method = "lambda$shear$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$dropItem(ServerLevel level, Entity entity) {
        EntityDropItemEvent event = new EntityDropItemEvent(((MushroomCow) (Object) this).getBukkitEntity(), (org.bukkit.entity.Item) entity.getBukkitEntity());
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled() && level.addFreshEntity(entity);
    }
    // CraftBukkit end
}
