package com.mohistmc.silkard.mixin.vanilla.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import org.bukkit.event.entity.SheepDyeWoolEvent;
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
@Mixin(DyeItem.class)
public class MixinDyeItem {

    @Unique
    private SheepDyeWoolEvent silkard$dyeEvent;

    @Inject(method = "interactLivingEntity", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void silkard$dyeWool(ItemStack itemStack, Player player, LivingEntity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(entity instanceof Sheep sheep)) {
            return;
        }
        DyeColor color = itemStack.get(DataComponents.DYE);
        if (color == null || sheep.getColor() == color) {
            return;
        }

        SheepDyeWoolEvent event = new SheepDyeWoolEvent((org.bukkit.entity.Sheep) sheep.getBukkitEntity(),
                org.bukkit.DyeColor.getByWoolData((byte) color.getId()), (org.bukkit.entity.Player) player.getBukkitEntity());
        sheep.level().getCraftServer().getPluginManager().callEvent(event);
        this.silkard$dyeEvent = event;
        if (event.isCancelled()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Redirect(method = "interactLivingEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;setColor(Lnet/minecraft/world/item/DyeColor;)V"))
    private void silkard$setColor(Sheep sheep, DyeColor color) {
        sheep.setColor(DyeColor.byId((byte) this.silkard$dyeEvent.getColor().getWoolData()));
    }
}
