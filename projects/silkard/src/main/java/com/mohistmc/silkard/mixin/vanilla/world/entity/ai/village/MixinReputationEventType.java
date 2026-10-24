package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.village;

import com.mohistmc.silkard.injected.world.entity.ai.village.ContextReputationEventType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ReputationEventType.class)
public interface MixinReputationEventType extends ContextReputationEventType {
}
