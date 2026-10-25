package com.mohistmc.silkard.mixin.vanilla.world.level.levelgen;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(NoiseBasedChunkGenerator.class)
public class MixinNoiseBasedChunkGenerator {

    @Inject(method = "debugPreliminarySurfaceLevel", at = @At("HEAD"))
    private void silkard_fillFromNoise(CallbackInfoReturnable<BlockState> cir) {
        // CraftBukkit
    }
}
