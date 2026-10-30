package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.golem;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(CopperGolem.class)
public class MixinCopperGolem {

    // CraftBukkit start - fire PlayerShearEntityEvent
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void silkard$shearEvent(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!CraftEventFactory.handlePlayerShearEntityEvent(player, (CopperGolem) (Object) this, itemstack, hand)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
    // CraftBukkit end

    // CraftBukkit start - fire EntityChangeBlockEvent when turning into a statue
    @Redirect(method = "turnToStatue", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$setBlock(ServerLevel level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        if (!CraftEventFactory.callEntityChangeBlockEvent((CopperGolem) (Object) this, pos, state)) {
            return false;
        }
        return level.setBlockAndUpdate(pos, state);
    }
    // CraftBukkit end

    // CraftBukkit start - preserved equipment always drops
    @Redirect(method = "turnToStatue", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/golem/CopperGolem;dropPreservedEquipment(Lnet/minecraft/server/level/ServerLevel;)V"))
    private void silkard$forceDropsOnStatue(CopperGolem self, ServerLevel level) {
        ((ContextLivingEntity) (Object) self).silkard$forceDrops(true);
        self.dropPreservedEquipment(level);
        ((ContextLivingEntity) (Object) self).silkard$forceDrops(false);
    }

    @Redirect(method = "shear", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/golem/CopperGolem;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private net.minecraft.world.entity.item.ItemEntity silkard$forceDropsOnShear(CopperGolem self, ServerLevel level, ItemStack stack, float offset) {
        ((ContextLivingEntity) (Object) self).silkard$forceDrops(true);
        net.minecraft.world.entity.item.ItemEntity dropped = self.spawnAtLocation(level, stack, offset);
        ((ContextLivingEntity) (Object) self).silkard$forceDrops(false);
        return dropped;
    }
    // CraftBukkit end

    // CraftBukkit start - add Bukkit remove cause
    @Redirect(method = "turnToStatue", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/golem/CopperGolem;discard()V"))
    private void silkard$discard(CopperGolem self) {
        ((ContextEntity) (Object) this).discard(EntityRemoveEvent.Cause.DESPAWN);
    }
    // CraftBukkit end
}
