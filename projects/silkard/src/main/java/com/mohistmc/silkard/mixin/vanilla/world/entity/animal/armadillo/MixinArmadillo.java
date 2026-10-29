package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.armadillo;

import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(Armadillo.class)
public class MixinArmadillo {

    // CraftBukkit start - shed scutes always drop
    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void silkard$forceDropsOn(ServerLevel level, CallbackInfo ci) {
        ((ContextLivingEntity) (Object) this).silkard$forceDrops(true);
    }

    @Inject(method = "customServerAiStep", at = @At("RETURN"))
    private void silkard$forceDropsOff(ServerLevel level, CallbackInfo ci) {
        ((ContextLivingEntity) (Object) this).silkard$forceDrops(false);
    }

    // CraftBukkit start - brushed scutes always drop
    @Inject(method = "brushOffScute", at = @At("HEAD"))
    private void silkard$brushForceDropsOn(Entity entity, ItemStack tool, CallbackInfoReturnable<Boolean> cir) {
        ((ContextLivingEntity) (Object) this).silkard$forceDrops(true);
    }

    @Inject(method = "brushOffScute", at = @At("RETURN"))
    private void silkard$brushForceDropsOff(Entity entity, ItemStack tool, CallbackInfoReturnable<Boolean> cir) {
        ((ContextLivingEntity) (Object) this).silkard$forceDrops(false);
    }
    // CraftBukkit end
}
