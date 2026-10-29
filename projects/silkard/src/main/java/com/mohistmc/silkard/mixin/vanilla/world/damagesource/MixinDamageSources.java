package com.mohistmc.silkard.mixin.vanilla.world.damagesource;

import com.mohistmc.silkard.injected.world.damagesource.ContextDamageSource;
import com.mohistmc.silkard.injected.world.damagesource.ContextDamageSources;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(DamageSources.class)
public class MixinDamageSources implements ContextDamageSources {

    // CraftBukkit start
    @Unique
    private DamageSource silkard$melting;

    @Unique
    private DamageSource silkard$poison;

    @Inject(method = "<init>(Lnet/minecraft/core/RegistryAccess;)V", at = @At("RETURN"))
    private void silkard$init(RegistryAccess registries, CallbackInfo ci) {
        this.silkard$melting = ((ContextDamageSource) ((DamageSources) (Object) this).source(DamageTypes.ON_FIRE)).melting();
        this.silkard$poison = ((ContextDamageSource) ((DamageSources) (Object) this).source(DamageTypes.MAGIC)).poison();
    }

    @Override
    public DamageSource melting() {
        return this.silkard$melting;
    }

    @Override
    public DamageSource poison() {
        return this.silkard$poison;
    }

    @Override
    public DamageSource explosion(@Nullable Entity entity, @Nullable Entity entity1, ResourceKey<DamageType> resourceKey) {
        return ((DamageSources) (Object) this).source(resourceKey, entity, entity1);
    }
    // CraftBukkit end

    @Override
    public DamageSource badRespawnPointExplosion(Vec3 vec3, BlockState blockState) {
        return ((ContextDamageSource) (Object) ((DamageSources) (Object) this).badRespawnPointExplosion(vec3)).directBlockState(blockState);
    }
}
