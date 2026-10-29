package com.mohistmc.silkard.mixin.vanilla.world.level;

import com.mohistmc.silkard.injected.world.level.ContextSpawnState;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(net.minecraft.world.level.NaturalSpawner.SpawnState.class)
public class MixinNaturalSpawner$SpawnState implements ContextSpawnState {

    @Shadow
    @Final
    private int spawnableChunkCount;

    @Shadow
    @Final
    private it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<MobCategory> mobCategoryCounts;

    // CraftBukkit start - per-world spawn limits
    @Override
    public boolean canSpawnForCategory(MobCategory mobCategory, int limit) {
        int i = limit * this.spawnableChunkCount / 289;
        return this.mobCategoryCounts.getInt(mobCategory) < i;
    }
    // CraftBukkit end
}
