package com.mohistmc.silkard.mixin.vanilla.world.level.levelgen.feature;

import com.mohistmc.silkard.bukkit.EndPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.feature.EndPlatformFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(EndPlatformFeature.class)
public class MixinEndPlatformFeature {

    // CraftBukkit start
    @Overwrite
    public static void createEndPlatform(ServerLevelAccessor level, BlockPos origin, boolean dropResources) {
        EndPlatformHelper.createEndPlatform(level, origin, dropResources, null);
    }
    // CraftBukkit end
}
