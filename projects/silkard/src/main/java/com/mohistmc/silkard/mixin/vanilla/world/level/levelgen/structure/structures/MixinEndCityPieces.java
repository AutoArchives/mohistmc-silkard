package com.mohistmc.silkard.mixin.vanilla.world.level.levelgen.structure.structures;

import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(EndCityPieces.class)
public class MixinEndCityPieces {

    @Inject(method = "addPiece", at = @At("HEAD"))
    private static void silkard_addPiece(CallbackInfoReturnable<EndCityPieces.EndCityPiece> cir) {
        // CraftBukkit
    }
}
