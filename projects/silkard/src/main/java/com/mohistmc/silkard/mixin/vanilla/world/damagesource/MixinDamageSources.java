package com.mohistmc.silkard.mixin.vanilla.world.damagesource;

import com.mohistmc.silkard.injected.world.damagesource.ContextDamageSource;
import com.mohistmc.silkard.injected.world.damagesource.ContextDamageSources;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.phys.Vec3;
import org.bukkit.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(DamageSources.class)
public class MixinDamageSources implements ContextDamageSources {

    @Override
    public DamageSource badRespawnPointExplosion(Vec3 vec3, BlockState blockState) {
        return ((ContextDamageSource) (Object) ((DamageSources) (Object) this).badRespawnPointExplosion(vec3)).directBlockState(blockState);
    }
}
