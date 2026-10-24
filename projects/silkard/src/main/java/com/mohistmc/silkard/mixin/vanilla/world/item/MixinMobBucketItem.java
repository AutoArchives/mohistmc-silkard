package com.mohistmc.silkard.mixin.vanilla.world.item;

import net.minecraft.world.item.MobBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(MobBucketItem.class)
public class MixinMobBucketItem {

    // 26.3 的 MobBucketItem 不再声明 useOn（只在 Item），原钩子待重定位 → require=0 暂挂
    @Inject(method = "useOn", require = 0, at = @At("HEAD"))
    private void silkard_useOn(CallbackInfo ci) {
        // CraftBukkit
    }
}
