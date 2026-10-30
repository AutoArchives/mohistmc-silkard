package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.equine;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mohistmc.silkard.injected.world.ContextContainer;
import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import com.mohistmc.silkard.injected.world.entity.animal.equine.ContextAbstractHorse;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/5/1 16:57
 */
@Mixin(AbstractHorse.class)
public class MixinAbstractHorse implements ContextAbstractHorse {

    @Unique
    public int maxDomestication = 100; // CraftBukkit - store max domestication value

    public int silkard$maxDomestication() {
        return maxDomestication;
    }

    public void silkard$maxDomestication(int maxDomestication) {
        this.maxDomestication = maxDomestication;
    }

    @Overwrite
    public int getMaxTemper() {
        return this.maxDomestication;
    }

    @ModifyExpressionValue(method = "createInventory", at = @At(value = "NEW", target = "Lnet/minecraft/world/SimpleContainer;"))
    private SimpleContainer silkard$inventoryOwner(SimpleContainer original) {
        ((ContextContainer) (Object) original).setOwner((org.bukkit.entity.AbstractHorse) ((AbstractHorse) (Object) this).getBukkitEntity());
        return original;
    }

    @Redirect(method = "handleEating", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/equine/AbstractHorse;heal(F)V"))
    private void silkard$eatingHeal(AbstractHorse instance, float amount) {
        ((ContextLivingEntity) (Object) instance).heal(amount, EntityRegainHealthEvent.RegainReason.EATING);
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/equine/AbstractHorse;heal(F)V"))
    private void silkard$regenHeal(AbstractHorse instance, float amount) {
        ((ContextLivingEntity) (Object) instance).heal(amount, EntityRegainHealthEvent.RegainReason.REGEN);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void silkard$saveMaxDomestication(ValueOutput output, CallbackInfo ci) {
        output.putInt("Bukkit.MaxDomestication", this.maxDomestication);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void silkard$readMaxDomestication(ValueInput input, CallbackInfo ci) {
        this.maxDomestication = input.getIntOr("Bukkit.MaxDomestication", this.maxDomestication);
    }

    @Inject(method = "handleStartJump", at = @At("HEAD"), cancellable = true)
    private void silkard$horseJump(int jumpScale, CallbackInfo ci) {
        float power = jumpScale >= 90 ? 1.0F : 0.4F + 0.4F * (float) jumpScale / 90.0F;
        if (!CraftEventFactory.callHorseJumpEvent((AbstractHorse) (Object) this, power)) {
            ci.cancel();
        }
    }
}
