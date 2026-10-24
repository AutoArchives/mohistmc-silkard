package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder.Instance;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(StartAttacking.class)
public class MixinStartAttacking {

    /**
     * @author Mgazul
     * @reason CraftBukkit - EntityTargetEvent
     */
    @Overwrite
    private static boolean lambda$create$3(StartAttacking.StartAttackingCondition condition, StartAttacking.TargetFinder finder, MemoryAccessor attackTarget, MemoryAccessor cantReachSince, ServerLevel level, Mob body, long timestamp) {
        Optional<? extends LivingEntity> target = finder.get(level, body);
        if (target.isEmpty()) {
            return false;
        }

        LivingEntity targetEntity = target.get();
        if (!body.canAttack(targetEntity)) {
            return false;
        }

        // CraftBukkit start
        EntityTargetEvent event = CraftEventFactory.callEntityTargetLivingEvent(body, targetEntity, (targetEntity instanceof ServerPlayer) ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY);
        if (event.isCancelled()) {
            return false;
        }
        if (event.getTarget() == null) {
            attackTarget.erase();
            return true;
        }
        targetEntity = ((CraftLivingEntity) event.getTarget()).getHandle();
        // CraftBukkit end

        attackTarget.set(targetEntity);
        cantReachSince.erase();
        return true;
    }
}
