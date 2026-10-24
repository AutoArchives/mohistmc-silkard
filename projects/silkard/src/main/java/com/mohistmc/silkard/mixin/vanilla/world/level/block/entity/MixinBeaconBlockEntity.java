package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import com.mohistmc.silkard.bukkit.BukkitUtils;
import io.izzel.arclight.mixin.TransformAccess;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/4/2 17:44
 */
@Mixin(BeaconBlockEntity.class)
public class MixinBeaconBlockEntity {


    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static List getHumansInRange(Level level, BlockPos worldPosition, int i) {
        return BukkitUtils.getHumansInRange(level, worldPosition, i);
    }

    // CraftBukkit start - Bukkit API beacon effects
    private static int silkard$getEffectDuration(int levels) {
        return (9 + levels * 2) * 20;
    }

    private static int silkard$getAmplification(int levels, @Nullable Holder<MobEffect> primaryPower, @Nullable Holder<MobEffect> secondaryPower) {
        if (levels >= 4 && java.util.Objects.equals(primaryPower, secondaryPower)) {
            return 1;
        }
        return 0;
    }

    private boolean silkard$hasSecondaryEffect(int levels, @Nullable Holder<MobEffect> primaryPower, @Nullable Holder<MobEffect> secondaryPower) {
        if (levels < 4 || secondaryPower == null) {
            return false;
        }
        return true;
    }
    // CraftBukkit end

}
