package com.mohistmc.silkard.mixin.vanilla.world.level.storage;

import com.mohistmc.silkard.injected.world.level.storage.ContextLevelStorageAccess;
import com.mohistmc.silkard.injected.world.level.storage.ContextLevelStorageSource;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.ContentValidationException;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(LevelStorageSource.class)
public class MixinLevelStorageSource implements ContextLevelStorageSource {

    // CraftBukkit start
    @Override
    public LevelStorageSource.LevelStorageAccess validateAndCreateAccess(String levelId, ResourceKey<LevelStem> dimensionType) throws IOException, ContentValidationException {
        LevelStorageSource.LevelStorageAccess access = ((LevelStorageSource) (Object) this).validateAndCreateAccess(levelId);
        ((ContextLevelStorageAccess) access).silkard$setDimType(dimensionType);
        return access;
    }

    public LevelStorageSource.LevelStorageAccess createAccess(String levelId, ResourceKey<LevelStem> dimensionType) throws IOException {
        LevelStorageSource.LevelStorageAccess access = ((LevelStorageSource) (Object) this).createAccess(levelId);
        ((ContextLevelStorageAccess) access).silkard$setDimType(dimensionType);
        return access;
    }

    public static Path getPre261StorageFolder(Path path, ResourceKey<LevelStem> dimensionType) {
        if (dimensionType == LevelStem.OVERWORLD) {
            return path;
        } else if (dimensionType == LevelStem.NETHER) {
            return path.resolve("DIM-1");
        } else if (dimensionType == LevelStem.END) {
            return path.resolve("DIM1");
        } else {
            return path.resolve("dimensions").resolve(dimensionType.identifier().getNamespace()).resolve(dimensionType.identifier().getPath());
        }
    }
    // CraftBukkit end
}
