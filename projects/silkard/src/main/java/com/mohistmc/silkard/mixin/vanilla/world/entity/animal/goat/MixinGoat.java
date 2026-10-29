package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.goat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(Goat.class)
public class MixinGoat {

    @Unique
    private PlayerBucketFillEvent silkard$milkEvent;

    // CraftBukkit - got milk?
    @Inject(method = "mobInteract", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/goat/Goat;getMilkingSound()Lnet/minecraft/sounds/SoundEvent;"))
    private void silkard$bucketFill(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Goat self = (Goat) (Object) this;
        this.silkard$milkEvent = CraftEventFactory.callPlayerBucketFillEvent((ServerLevel) player.level(), player, self.blockPosition(), self.blockPosition(), null,
                player.getItemInHand(hand), Items.MILK_BUCKET, hand);
        if (this.silkard$milkEvent.isCancelled()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemUtils;createFilledResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack silkard$milkResult(ItemStack itemstack, Player player, ItemStack filled) {
        return ItemUtils.createFilledResult(itemstack, player, CraftItemStack.asNMSCopy(this.silkard$milkEvent.getItemStack()));
    }
}
