package com.mohistmc.silkard.mixin.vanilla.world.level.levelgen.structure.structures;

import net.minecraft.world.level.levelgen.structure.structures.ShipwreckPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ShipwreckPieces.class)
public class MixinShipwreckPieces {

    // CraftBukkit - setCraftLootTable 的调用点在嵌套 ShipwreckPiece 内，外层类无 addPiece
    @Inject(method = "addRandomPiece", at = @At("HEAD"), require = 0)
    private static void silkard_addPiece(CallbackInfoReturnable<ShipwreckPieces.ShipwreckPiece> cir) {
    }
}
