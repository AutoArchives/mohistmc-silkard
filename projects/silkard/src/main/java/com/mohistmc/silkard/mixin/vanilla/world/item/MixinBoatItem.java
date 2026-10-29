package com.mohistmc.silkard.mixin.vanilla.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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
@Mixin(BoatItem.class)
public class MixinBoatItem {

    @Unique
    private InteractionResult silkard$result = null;

    // CraftBukkit - PlayerInteractEvent
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BoatItem;getBoat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/HitResult;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;"), cancellable = true)
    private void silkard$interact(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, @Local HitResult hitResult) {
        BlockHitResult blockHit = (BlockHitResult) hitResult;
        PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, blockHit.getBlockPos(), blockHit.getDirection(), player.getItemInHand(hand), false, hand, blockHit.getLocation());

        if (event.isCancelled()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    // CraftBukkit - EntityPlaceEvent
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$addFreshEntity(Level level, Entity entity, @Local(argsOnly = true) Player player, @Local(argsOnly = true) InteractionHand hand, @Local HitResult hitResult) {
        BlockHitResult blockHit = (BlockHitResult) hitResult;

        if (CraftEventFactory.callEntityPlaceEvent(level, blockHit.getBlockPos(), blockHit.getDirection(), player, entity, hand).isCancelled()) {
            this.silkard$result = InteractionResult.FAIL;
            return false;
        }

        if (!level.addFreshEntity(entity)) {
            this.silkard$result = InteractionResult.PASS;
            return false;
        }
        return true;
    }

    @Inject(method = "use", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), cancellable = true)
    private void silkard$placeResult(CallbackInfoReturnable<InteractionResult> cir) {
        if (this.silkard$result != null) {
            cir.setReturnValue(this.silkard$result);
            this.silkard$result = null;
        }
    }
}
