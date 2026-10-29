package com.mohistmc.silkard.mixin.vanilla.util;

import com.mohistmc.silkard.injected.util.ContextTickThrottler;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.TickThrottler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(TickThrottler.class)
public class MixinTickThrottler implements ContextTickThrottler {

    @Shadow
    private int incrementStep;

    @Shadow
    private int threshold;

    // CraftBukkit - use thread-safe field access instead
    @Unique
    private final AtomicInteger silkard$count = new AtomicInteger();

    @Overwrite
    public void increment() {
        this.silkard$count.addAndGet(this.incrementStep);
    }

    @Overwrite
    public void tick() {
        for (int val; (val = this.silkard$count.get()) > 0 && !this.silkard$count.compareAndSet(val, val - 1); ) ;
    }

    @Overwrite
    public boolean isUnderThreshold() {
        return this.silkard$count.get() < this.threshold;
    }

    @Override
    public boolean isIncrementAndUnderThreshold() {
        return this.isIncrementAndUnderThreshold(this.incrementStep, this.threshold);
    }

    @Override
    public boolean isIncrementAndUnderThreshold(int incrementStep, int threshold) {
        return this.silkard$count.addAndGet(incrementStep) < threshold;
    }
}
