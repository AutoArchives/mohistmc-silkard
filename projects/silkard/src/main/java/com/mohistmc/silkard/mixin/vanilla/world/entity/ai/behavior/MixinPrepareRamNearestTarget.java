package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.PrepareRamNearestTarget;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(PrepareRamNearestTarget.class)
public abstract class MixinPrepareRamNearestTarget {

    @Invoker("chooseRamPosition")
    protected abstract void silkard$chooseRamPosition(PathfinderMob body, LivingEntity target);

    @Redirect(method = "lambda$start$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/PrepareRamNearestTarget;chooseRamPosition(Lnet/minecraft/world/entity/PathfinderMob;Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void silkard$choose(PrepareRamNearestTarget<?> self, PathfinderMob body, LivingEntity target) {
        EntityTargetEvent event = CraftEventFactory.callEntityTargetLivingEvent(body, target, target instanceof ServerPlayer ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY);
        if (event.isCancelled() || event.getTarget() == null) {
            return;
        }
        this.silkard$chooseRamPosition(body, ((CraftLivingEntity) event.getTarget()).getHandle());
    }
}
