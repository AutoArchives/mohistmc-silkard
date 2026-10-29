package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.block.ContextChargeCursor;
import net.minecraft.world.level.block.SculkSpreader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(SculkSpreader.ChargeCursor.class)
public class MixinSculkSpreader$ChargeCursor implements ContextChargeCursor {

    @Shadow
    private int charge;

    @Override
    public void silkard$setCharge(int charge) {
        this.charge = charge;
    }
}
