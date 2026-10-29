package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.golem;

import com.mohistmc.silkard.injected.world.damagesource.ContextDamageSources;
import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(SnowGolem.class)
public class MixinSnowGolem {

    // CraftBukkit start - fire EntityBlockFormEvent when the snow block forms
    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$setBlock(Level level, BlockPos pos, BlockState state) {
        if (!CraftEventFactory.handleBlockFormEvent(level, pos, state, (SnowGolem) (Object) this)) {
            return false;
        }
        return level.setBlockAndUpdate(pos, state);
    }
    // CraftBukkit end

    // CraftBukkit - DamageSources.ON_FIRE -> melting
    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;onFire()Lnet/minecraft/world/damagesource/DamageSource;"))
    private net.minecraft.world.damagesource.DamageSource silkard$melting(DamageSources sources) {
        return ((ContextDamageSources) (Object) sources).melting();
    }
    // CraftBukkit end

    // CraftBukkit start - fire PlayerShearEntityEvent
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void silkard$shearEvent(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!CraftEventFactory.handlePlayerShearEntityEvent(player, (SnowGolem) (Object) this, itemstack, hand)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
    // CraftBukkit end

    // CraftBukkit start - sheared pumpkin always drops
    @Inject(method = "shear", at = @At("HEAD"))
    private void silkard$forceDropsOn(net.minecraft.server.level.ServerLevel level, SoundSource sound, ItemStack tool, CallbackInfo ci) {
        ((ContextLivingEntity) (Object) this).silkard$forceDrops(true);
    }

    @Inject(method = "shear", at = @At("RETURN"))
    private void silkard$forceDropsOff(net.minecraft.server.level.ServerLevel level, SoundSource sound, ItemStack tool, CallbackInfo ci) {
        ((ContextLivingEntity) (Object) this).silkard$forceDrops(false);
    }
    // CraftBukkit end
}
