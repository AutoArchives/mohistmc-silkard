package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.sensing;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(TemptingSensor.class)
@SuppressWarnings({"rawtypes", "unchecked"})
public class MixinTemptingSensor {

    // CraftBukkit start - EntityTargetEvent
    @WrapOperation(method = "doTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/Brain;setMemory(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/lang/Object;)V"))
    private void silkard$temptTarget(Brain<?> brain, MemoryModuleType<?> memory, Object value, Operation<Void> original, ServerLevel level, PathfinderMob body) {
        EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(body, (LivingEntity) value, EntityTargetEvent.TargetReason.TEMPT);
        if (event.isCancelled()) {
            return;
        }
        if (event.getTarget() instanceof HumanEntity) {
            original.call(brain, memory, ((CraftHumanEntity) event.getTarget()).getHandle());
        } else {
            brain.eraseMemory(memory);
        }
    }
    // CraftBukkit end
}
