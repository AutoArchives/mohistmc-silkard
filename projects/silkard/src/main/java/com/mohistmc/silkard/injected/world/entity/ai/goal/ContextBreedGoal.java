package com.mohistmc.silkard.injected.world.entity.ai.goal;

import com.mohistmc.silkard.util.ContextStateException;
import net.minecraft.world.entity.animal.Animal;

public interface ContextBreedGoal {

    default Animal silkard$animal() {
        throw new ContextStateException("Not implemented");
    }

    default Animal silkard$partner() {
        throw new ContextStateException("Not implemented");
    }
}
