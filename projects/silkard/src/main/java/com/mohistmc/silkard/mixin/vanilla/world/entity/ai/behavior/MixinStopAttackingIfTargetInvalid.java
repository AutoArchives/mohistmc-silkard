package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder.Instance;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(StopAttackingIfTargetInvalid.class)
public class MixinStopAttackingIfTargetInvalid {

    @Shadow
    private static boolean isTiredOfTryingToReachTarget(LivingEntity body, java.util.Optional<Long> cantReachSince) {
        throw new AssertionError();
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - EntityTargetEvent
     */
    @Overwrite
    private static boolean lambda$create$6(Instance instance, MemoryAccessor attackTarget, boolean canGrowTiredOfTryingToReachTarget, MemoryAccessor cantReachSince, StopAttackingIfTargetInvalid.StopAttackCondition stopAttackingWhen, StopAttackingIfTargetInvalid.TargetErasedCallback onTargetErased, ServerLevel level, Mob body, long timestamp) {
        LivingEntity target = (LivingEntity) instance.get(attackTarget);
        if (body.canAttack(target)
                && (!canGrowTiredOfTryingToReachTarget || !isTiredOfTryingToReachTarget(body, instance.tryGet(cantReachSince)))
                && target.isAlive()
                && target.level() == body.level()
                && !stopAttackingWhen.test(level, target)) {
            return true;
        } else {
            // CraftBukkit start
            LivingEntity old = body.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
            EntityTargetEvent event = CraftEventFactory.callEntityTargetLivingEvent(body, null, (old != null && !old.isAlive()) ? EntityTargetEvent.TargetReason.TARGET_DIED : EntityTargetEvent.TargetReason.FORGOT_TARGET);
            if (event.isCancelled()) {
                return false;
            }
            if (event.getTarget() != null) {
                body.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, ((CraftLivingEntity) event.getTarget()).getHandle());
                return true;
            }
            // CraftBukkit end
            onTargetErased.accept(level, body, target);
            attackTarget.erase();
            return true;
        }
    }
}
