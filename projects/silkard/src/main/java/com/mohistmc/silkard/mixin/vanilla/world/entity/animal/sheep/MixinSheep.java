package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.sheep;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(Sheep.class)
public class MixinSheep {

    // CraftBukkit start - shear event
    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V"), cancellable = true)
    private void silkard$playerShear(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, @Local(ordinal = 0) ItemStack itemstack) {
        if (!CraftEventFactory.handlePlayerShearEntityEvent(player, (Sheep) (Object) this, itemstack, hand)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @WrapOperation(method = "lambda$shear$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity silkard$forceDrops(Sheep instance, ServerLevel level, ItemStack stack, float offset, Operation<ItemEntity> original) {
        ((ContextLivingEntity) instance).silkard$forceDrops(true);
        ItemEntity itemEntity = original.call(instance, level, stack, offset);
        ((ContextLivingEntity) instance).silkard$forceDrops(false);
        return itemEntity;
    }

    @Inject(method = "ate", at = @At("HEAD"), cancellable = true)
    private void silkard$regrowWool(CallbackInfo ci) {
        SheepRegrowWoolEvent event = new SheepRegrowWoolEvent((org.bukkit.entity.Sheep) ((Sheep) (Object) this).getBukkitEntity());
        ((Sheep) (Object) this).level().getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
