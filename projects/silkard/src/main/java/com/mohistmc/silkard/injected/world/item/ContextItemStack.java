package com.mohistmc.silkard.injected.world.item;

import com.mohistmc.silkard.util.ContextStateException;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public interface ContextItemStack {

    @Deprecated
    default void setItem(Holder<Item> item) {
        throw new ContextStateException("Not implemented");
    }

    default PatchedDataComponentMap getComponentsClone() {
        throw new ContextStateException("Not implemented");
    }

    default void setComponentsClone(@Nullable PatchedDataComponentMap patchedDataComponentMap) {
        throw new ContextStateException("Not implemented");
    }

    default void restorePatch(DataComponentPatch empty) {
        throw new ContextStateException("Not implemented");
    }

    default DataComponentPatch asPatch() {
        throw new ContextStateException("Not implemented");
    }

    default void silkard$fakeShrink(int count) {
        throw new ContextStateException("Not implemented");
    }
}
