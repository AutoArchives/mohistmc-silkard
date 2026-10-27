package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.breeze;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.breeze.Breeze;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(Breeze.class)
public class MixinBreeze {

    // CraftBukkit - SPIGOT-7957: allow attacking whatever the brain picked
    @Inject(method = "canAttack", cancellable = true, at = @At("HEAD"))
    private void silkard$brainTarget(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity brainTarget = ((Breeze) (Object) this).getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        if (brainTarget != null) {
            cir.setReturnValue(target.is(brainTarget));
        }
    }
}
