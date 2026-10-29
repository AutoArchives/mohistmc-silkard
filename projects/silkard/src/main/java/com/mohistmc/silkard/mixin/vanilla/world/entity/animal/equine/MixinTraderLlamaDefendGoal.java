package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.equine;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(targets = "net.minecraft.world.entity.animal.equine.TraderLlama$TraderLlamaDefendWanderingTraderGoal")
public class MixinTraderLlamaDefendGoal {

    // CraftBukkit - EntityTargetEvent
    @Redirect(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void silkard$setTarget(Mob mob, LivingEntity target) {
        mob.setTarget(target, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true);
    }
}
