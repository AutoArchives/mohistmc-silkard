package com.mohistmc.silkard.mixin.vanilla.network.syncher;

import com.mohistmc.silkard.injected.network.syncher.ContextSynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(SynchedEntityData.class)
public class MixinSynchedEntityData implements ContextSynchedEntityData {

    @Shadow
    private boolean isDirty;

    @Shadow
    private <T> SynchedEntityData.DataItem<T> getItem(EntityDataAccessor<T> accessor) {
        throw new AssertionError();
    }

    @Override
    public <T> void markDirty(EntityDataAccessor<T> accessor) {
        this.getItem(accessor).setDirty(true);
        this.isDirty = true;
    }
}
