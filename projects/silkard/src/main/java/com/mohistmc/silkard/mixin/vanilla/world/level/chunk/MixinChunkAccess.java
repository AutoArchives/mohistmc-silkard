package com.mohistmc.silkard.mixin.vanilla.world.level.chunk;

import com.mohistmc.silkard.injected.world.level.chunk.ContextChunkAccess;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.StructureAccess;
import org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/4/2 17:24
 */
@Mixin(ChunkAccess.class)
public abstract class MixinChunkAccess implements LightChunk, StructureAccess, BiomeResolver, AttachmentTarget, ContextChunkAccess {

    @Shadow
    @Final
    protected LevelChunkSection[] sections;

    // CraftBukkit start - SPIGOT-6814: move to IChunkAccess to account for 1.17 to 1.18 chunk upgrading.
    private static final org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY = new org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry();
    public org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer persistentDataContainer = new org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer(DATA_TYPE_REGISTRY);
    // CraftBukkit end

    @Override
    public DirtyCraftPersistentDataContainer silkard_persistentDataContainer() {
        return persistentDataContainer;
    }

    // CraftBukkit start
    @Override
    public void setBiome(int i, int j, int k, Holder<Biome> biome) {
        try {
            int l = QuartPos.fromBlock(this.getMinY());
            int i1 = l + QuartPos.fromBlock(this.getHeight()) - 1;
            int j1 = Mth.clamp(j, l, i1);
            int k1 = this.getSectionIndex(QuartPos.toBlock(j1));

            this.sections[k1].setBiome(i & 3, j1 & 3, k & 3, biome);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Setting biome");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Biome being set");

            crashreportcategory.setDetail("Location", () -> {
                return CrashReportCategory.formatLocation(this, i, j, k);
            });
            throw new ReportedException(crashreport);
        }
    }
    // CraftBukkit end

}
