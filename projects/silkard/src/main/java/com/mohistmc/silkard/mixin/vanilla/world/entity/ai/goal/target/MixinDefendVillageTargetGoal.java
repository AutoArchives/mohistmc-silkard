package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.goal.target;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(DefendVillageTargetGoal.class)
public class MixinDefendVillageTargetGoal {

    @Redirect(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/golem/IronGolem;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void silkard$setTarget(IronGolem golem, LivingEntity target) {
        golem.setTarget(target, EntityTargetEvent.TargetReason.DEFEND_VILLAGE, true); // CraftBukkit - reason
    }
}
