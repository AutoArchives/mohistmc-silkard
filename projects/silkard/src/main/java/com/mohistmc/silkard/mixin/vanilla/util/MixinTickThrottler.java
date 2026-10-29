package com.mohistmc.silkard.mixin.vanilla.util;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.TickThrottler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(TickThrottler.class)
public class MixinTickThrottler {

    @Shadow
    @Final
    private int incrementStep;

    @Shadow
    @Final
    private int threshold;

    @Unique
    private final AtomicInteger silkard$count = new AtomicInteger();

    @Overwrite
    public void increment() {
        this.silkard$count.addAndGet(this.incrementStep);
    }

    @Overwrite
    public void tick() {
        for (int val; (val = this.silkard$count.get()) > 0 && !this.silkard$count.compareAndSet(val, val - 1); ) {
        }
    }

    @Overwrite
    public boolean isUnderThreshold() {
        return this.silkard$count.get() < this.threshold;
    }

    public boolean isIncrementAndUnderThreshold() {
        return this.isIncrementAndUnderThreshold(this.incrementStep, this.threshold);
    }

    public boolean isIncrementAndUnderThreshold(int incrementStep, int threshold) {
        return this.silkard$count.addAndGet(incrementStep) < threshold;
    }
}
