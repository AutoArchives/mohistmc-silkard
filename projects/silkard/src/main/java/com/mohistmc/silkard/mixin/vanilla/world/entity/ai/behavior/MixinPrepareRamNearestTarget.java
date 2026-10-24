package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.PrepareRamNearestTarget;
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
@Mixin(PrepareRamNearestTarget.class)
public class MixinPrepareRamNearestTarget {

    @Shadow
    private void chooseRamPosition(PathfinderMob body, LivingEntity ramableTarget) {
        throw new AssertionError();
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - EntityTargetEvent
     */
    @Overwrite
    private void lambda$start$2(PathfinderMob body, LivingEntity livingentity) {
        // CraftBukkit start
        EntityTargetEvent event = CraftEventFactory.callEntityTargetLivingEvent(body, livingentity, (livingentity instanceof ServerPlayer) ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY);
        if (event.isCancelled() || event.getTarget() == null) {
            return;
        }
        livingentity = ((CraftLivingEntity) event.getTarget()).getHandle();
        // CraftBukkit end
        this.chooseRamPosition(body, livingentity);
    }
}
