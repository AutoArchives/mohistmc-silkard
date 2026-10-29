package com.mohistmc.silkard.mixin.vanilla.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(HangingEntityItem.class)
public class MixinHangingEntityItem {

    // CraftBukkit - HangingPlaceEvent
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/HangingEntity;playPlacementSound()V"), cancellable = true)
    private void silkard$hangingPlace(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Local HangingEntity entity) {
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        HangingPlaceEvent event = new HangingPlaceEvent((org.bukkit.entity.Hanging) entity.getBukkitEntity(),
                context.getPlayer() == null ? null : (org.bukkit.entity.Player) context.getPlayer().getBukkitEntity(),
                level.getWorld().getBlockAt(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ()),
                CraftBlock.notchToBlockFace(context.getClickedFace()),
                CraftEquipmentSlot.getHand(context.getHand()),
                CraftItemStack.asBukkitCopy(stack));
        level.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
