package com.mohistmc.silkard.injected.core.dispenser;

import com.mohistmc.silkard.util.ContextStateException;

/**
 * 对应 CraftBukkit 为 DefaultDispenseItemBehavior 增加的 dropper 构造标记。
 * CraftBukkit: new DefaultDispenseItemBehavior(boolean dropper) — DropperBlock 使用。
 */
public interface ContextDefaultDispenseItemBehavior {

    default void silkard$dropper(boolean dropper) {
        throw new ContextStateException("Not implemented");
    }

    default boolean silkard$dropper() {
        throw new ContextStateException("Not implemented");
    }
}
