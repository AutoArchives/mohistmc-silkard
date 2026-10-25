package com.mohistmc.silkard.mixin.vanilla.world.level.storage.loot.predicates;

import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ExplosionCondition.class)
public class MixinExplosionCondition {

    @Inject(method = { "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z" }, at = @At("HEAD"))
    private void silkard_test(CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit
    }
}
