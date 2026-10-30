package com.mohistmc.silkard.mixin.vanilla.util.worldupdate;

import com.mojang.datafixers.DataFixer;
import com.mohistmc.silkard.injected.world.level.storage.ContextLevelStorageAccess;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/31
 */
@Mixin(WorldUpgrader.class)
public class MixinWorldUpgrader {

    // @formatter:off
    @Shadow @Final @Mutable private Set<ResourceKey<Level>> levels;
    // @formatter:on

    // CraftBukkit start - only upgrade the selected dimension
    @Inject(method = "<init>", at = @At("RETURN"))
    private void silkard$upgradeDimension(LevelStorageSource.LevelStorageAccess levelStorage, DataFixer dataFixer, RegistryAccess registryAccess, boolean eraseCache, boolean recreateRegionFiles, CallbackInfo ci) {
        ResourceKey<LevelStem> typeKey = ((ContextLevelStorageAccess) levelStorage).silkard$getTypeKey();
        if (typeKey != null) {
            this.levels = Stream.of(typeKey).map(Registries::levelStemToLevel).collect(Collectors.toUnmodifiableSet());
        }
    }
    // CraftBukkit end
}
