package com.mohistmc.silkard.mixin.vanilla.world.level;

import com.mohistmc.silkard.injected.world.level.ContextExplosion;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(Explosion.class)
public interface MixinExplosion extends ContextExplosion {

}
