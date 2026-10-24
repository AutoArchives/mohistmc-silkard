package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder.Instance;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(BabyFollowAdult.class)
public class MixinBabyFollowAdult {

    /**
     * @author Mgazul
     * @reason CraftBukkit - EntityTargetLivingEntityEvent
     */
    @Overwrite
    private static boolean lambda$create$3(Instance instance, MemoryAccessor nearestAdultAccessor, UniformInt followRange, boolean targetEye, Function<LivingEntity, Float> speedModifier, MemoryAccessor lookTarget, MemoryAccessor walkTarget, ServerLevel level, LivingEntity body, long timestamp) {
        if (!body.isBaby()) {
            return false;
        } else {
            LivingEntity adult = (LivingEntity) instance.get(nearestAdultAccessor);
            if (body.closerThan(adult, followRange.maxInclusive() + 1) && !body.closerThan(adult, followRange.minInclusive())) {
                // CraftBukkit start
                EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(body, adult, EntityTargetEvent.TargetReason.FOLLOW_LEADER);
                if (event.isCancelled()) {
                    return false;
                }
                if (event.getTarget() == null) {
                    walkTarget.erase();
                    return true;
                }
                adult = ((CraftLivingEntity) event.getTarget()).getHandle();
                // CraftBukkit end
                net.minecraft.world.entity.ai.memory.WalkTarget target = new net.minecraft.world.entity.ai.memory.WalkTarget(
                        new net.minecraft.world.entity.ai.behavior.EntityTracker(adult, targetEye, targetEye), speedModifier.apply(body), followRange.minInclusive() - 1
                );
                lookTarget.set(new net.minecraft.world.entity.ai.behavior.EntityTracker(adult, true, targetEye));
                walkTarget.set(target);
                return true;
            } else {
                return false;
            }
        }
    }
}
