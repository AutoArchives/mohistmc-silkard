package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.goal.target;

import com.mohistmc.silkard.injected.world.entity.ContextMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(TargetGoal.class)
public class MixinTargetGoal {

    // CraftBukkit start
    @Redirect(method = "canContinueToUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private boolean silkard$closestEntity(Mob mob, LivingEntity target) {
        return ((ContextMob) mob).setTarget(target, EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
    }

    @Redirect(method = "stop", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private boolean silkard$forgotTarget(Mob mob, LivingEntity target) {
        return ((ContextMob) mob).setTarget(target, EntityTargetEvent.TargetReason.FORGOT_TARGET, true);
    }
    // CraftBukkit end
}
