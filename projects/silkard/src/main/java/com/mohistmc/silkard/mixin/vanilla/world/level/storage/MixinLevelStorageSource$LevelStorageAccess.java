package com.mohistmc.silkard.mixin.vanilla.world.level.storage;

import com.mohistmc.silkard.injected.world.level.storage.ContextLevelStorageAccess;
import java.nio.file.Path;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(LevelStorageSource.LevelStorageAccess.class)
public class MixinLevelStorageSource$LevelStorageAccess implements ContextLevelStorageAccess {

    // CraftBukkit start
    @Unique
    private ResourceKey<LevelStem> silkard$dimensionType;

    @Override
    public ResourceKey<LevelStem> silkard$getTypeKey() {
        return this.silkard$dimensionType;
    }

    @Override
    public void silkard$setDimType(ResourceKey<LevelStem> typeKey) {
        this.silkard$dimensionType = typeKey;
    }

    @Redirect(method = "getDimensionPath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;getStorageFolder(Lnet/minecraft/resources/ResourceKey;Ljava/nio/file/Path;)Ljava/nio/file/Path;"))
    private Path silkard$getStorageFolder(ResourceKey<Level> name, Path path) {
        ResourceKey<LevelStem> typeKey = this.silkard$dimensionType;
        if (typeKey != null) {
            return DimensionType.getStorageFolder(Registries.levelStemToLevel(typeKey), path);
        }
        return DimensionType.getStorageFolder(name, path);
    }
    // CraftBukkit end
}
