package com.mohistmc.silkard.injected.world.entity;

import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Mob;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
public interface ContextConversionType {

    default void postConvert(Mob from, Mob to, ConversionParams params) {
    }
}
