package com.mohistmc.silkard.mixin.vanilla.world.level.chunk;

import com.mohistmc.silkard.injected.world.level.chunk.ContextLevelChunkSection;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(LevelChunkSection.class)
public class MixinLevelChunkSection implements ContextLevelChunkSection {

    @Shadow
    private PalettedContainerRO<Holder<Biome>> biomes;

    @Override
    public void setBiome(int i, int j, int k, Holder<Biome> biome) {
        // runtime instance is always a writable PalettedContainer, the field is only declared read-only
        ((PalettedContainer<Holder<Biome>>) this.biomes).set(i, j, k, biome);
    }
}
