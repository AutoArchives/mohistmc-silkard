package com.mohistmc.silkard.mixin.vanilla.world.entity;

import com.mohistmc.silkard.injected.world.entity.ContextMob;
import com.mohistmc.silkard.injected.world.entity.ContextNeutralMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(NeutralMob.class)
public interface MixinNeutralMob extends ContextNeutralMob {

    @Override
    default boolean setTarget(LivingEntity target, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        return ((ContextMob) this).setTarget(target, reason, fireEvent); // CraftBukkit
    }

    @Redirect(method = "readPersistentAngerSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/NeutralMob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void silkard$setAngerTarget(NeutralMob instance, LivingEntity target) {
        instance.setTarget(target, EntityTargetEvent.TargetReason.UNKNOWN, false); // CraftBukkit
    }

    @Redirect(method = "stopBeingAngry", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/NeutralMob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void silkard$forgetTarget(NeutralMob instance, LivingEntity target) {
        instance.setTarget(target, EntityTargetEvent.TargetReason.FORGOT_TARGET, true); // CraftBukkit
    }
}
