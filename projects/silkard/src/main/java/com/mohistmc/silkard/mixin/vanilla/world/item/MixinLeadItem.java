package com.mohistmc.silkard.mixin.vanilla.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.hanging.HangingPlaceEvent;
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
@Mixin(LeadItem.class)
public class MixinLeadItem {

    @Unique
    private static InteractionHand silkard$hand = InteractionHand.MAIN_HAND;

    // CraftBukkit - pass hand
    @Redirect(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/LeadItem;bindPlayerMobs(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/InteractionResult;"))
    private static InteractionResult silkard$bindPlayerMobs(Player player, Level level, BlockPos pos, @Local(argsOnly = true) UseOnContext context) {
        silkard$hand = context.getHand();
        return LeadItem.bindPlayerMobs(player, level, pos);
    }

    // CraftBukkit - HangingPlaceEvent
    @Inject(method = "bindPlayerMobs", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), cancellable = true)
    private static void silkard$hangingPlace(Player player, Level level, BlockPos pos, CallbackInfoReturnable<InteractionResult> cir, @Local java.util.Optional<LeashFenceKnotEntity> existingKnot, @Local LeashFenceKnotEntity knot) {
        if (existingKnot.isEmpty()) {
            HangingPlaceEvent event = new HangingPlaceEvent((org.bukkit.entity.Hanging) knot.getBukkitEntity(), player == null ? null : (org.bukkit.entity.Player) player.getBukkitEntity(), CraftBlock.at(level, pos), BlockFace.SELF, CraftEquipmentSlot.getHand(silkard$hand));
            level.getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                knot.discard(null);
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }

    // CraftBukkit - PlayerLeashEntityEvent
    @Redirect(method = "bindPlayerMobs", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;canHaveALeashAttachedTo(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean silkard$canHaveALeashAttachedTo(Leashable leashable, Entity knot, @Local(argsOnly = true) Player player) {
        if (!leashable.canHaveALeashAttachedTo(knot)) {
            return false;
        }
        return !(leashable instanceof Entity leashed) || !CraftEventFactory.callPlayerLeashEntityEvent(leashed, knot, player, silkard$hand).isCancelled();
    }

    // CraftBukkit - add Bukkit remove cause
    @Redirect(method = "bindPlayerMobs", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/LeashFenceKnotEntity;discard()V"))
    private static void silkard$discard(LeashFenceKnotEntity knot) {
        knot.discard(null);
    }
}
