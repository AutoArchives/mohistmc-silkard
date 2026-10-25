package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import net.minecraft.world.level.block.entity.BellBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(BellBlockEntity.class)
public class MixinBellBlockEntity {

    @Inject(method = "tick", at = @At("HEAD"))
    private static void silkard_tick(CallbackInfo ci) {
        // CraftBukkit
    }
}
