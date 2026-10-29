package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import java.util.List;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(BannerBlockEntity.class)
public class MixinBannerBlockEntity {

    @Shadow
    private BannerPatternLayers patterns;

    public void setPatterns(BannerPatternLayers bannerpatternlayers) {
        if (bannerpatternlayers.layers().size() > 20) {
            bannerpatternlayers = new BannerPatternLayers(List.copyOf(bannerpatternlayers.layers().subList(0, 20)));
        }
        this.patterns = bannerpatternlayers;
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void silkard$limitLoadedPatterns(ValueInput input, CallbackInfo ci) {
        this.setPatterns(this.patterns);
    }

    @Inject(method = "applyImplicitComponents", at = @At("TAIL"))
    private void silkard$limitComponentPatterns(DataComponentGetter components, CallbackInfo ci) {
        this.setPatterns(this.patterns);
    }
}
