package com.mohistmc.silkard.injected.world.entity.ai.goal;

import com.mohistmc.silkard.util.ContextStateException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;

public interface ContextMoveToBlockGoal {

    default PathfinderMob silkard$mob() {
        throw new ContextStateException("Not implemented");
    }

    default BlockPos silkard$blockPos() {
        throw new ContextStateException("Not implemented");
    }
}
