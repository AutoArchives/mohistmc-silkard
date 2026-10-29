package com.mohistmc.silkard.mixin.vanilla.world.entity;

import com.mohistmc.silkard.injected.world.entity.ContextConversionType;
import net.minecraft.world.entity.ConversionType;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(ConversionType.class)
public interface MixinConversionType extends ContextConversionType {
}
