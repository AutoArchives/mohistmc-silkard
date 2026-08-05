package com.mohistmc.silkard.mixin.vanilla.world.level.storage.loot.predicates;

import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ExplosionCondition.class)
public class MixinExplosionCondition {

    @Inject(method = "test*", at = @At("HEAD"))
    private void silkard_test(CallbackInfo ci) {
        // CraftBukkit
    }
}
