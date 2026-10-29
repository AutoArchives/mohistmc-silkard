package com.mohistmc.silkard.mixin.vanilla.world.entity.monster;

import com.mohistmc.silkard.injected.world.entity.ContextMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(targets = "net.minecraft.world.entity.monster.Phantom$PhantomAttackPlayerTargetGoal")
public class MixinPhantom$PhantomAttackPlayerTargetGoal {

    // CraftBukkit start - reason
    @Redirect(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Phantom;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void silkard$setTarget(Phantom instance, LivingEntity target) {
        ((ContextMob) instance).setTarget(target, EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);
    }
    // CraftBukkit end
}
