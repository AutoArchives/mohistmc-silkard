package com.mohistmc.silkard.mixin.vanilla.world.level.levelgen.structure.templatesystem;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(StructurePlaceSettings.class)
public class MixinStructurePlaceSettings {

    @Shadow
    public int palette;

    // CraftBukkit - Set initial value so we know if the palette has been set forcefully
    @Inject(method = "<init>", at = @At("RETURN"))
    private void silkard$initPalette(CallbackInfo ci) {
        this.palette = -1;
    }

    // CraftBukkit - forced palette selection
    @Inject(method = "getRandomPalette", at = @At("HEAD"), cancellable = true)
    private void silkard$forcedPalette(List<StructureTemplate.Palette> palettes, BlockPos pos, CallbackInfoReturnable<StructureTemplate.Palette> cir) {
        if (palettes.isEmpty()) {
            return;
        }

        if (this.palette >= 0) {
            if (this.palette >= palettes.size()) {
                throw new IllegalArgumentException("Palette index out of bounds. Got " + this.palette + " where there are only " + palettes.size() + " palettes available.");
            }
            cir.setReturnValue(palettes.get(this.palette));
        }
    }
}
