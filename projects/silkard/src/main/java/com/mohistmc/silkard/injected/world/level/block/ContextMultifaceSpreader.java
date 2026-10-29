package com.mohistmc.silkard.injected.world.level.block;

import net.minecraft.core.BlockPos;

public interface ContextMultifaceSpreader {

    ThreadLocal<BlockPos> SOURCE = new ThreadLocal<>();
}
