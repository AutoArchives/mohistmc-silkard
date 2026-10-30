package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/31
 */
@Mixin(PointedDripstoneBlock.class)
public class MixinPointedDripstoneBlock {

    // CraftBukkit - keep the block in the damage source so plugins get the stalagmite location
    @Redirect(method = "fallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;stalagmite()Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource silkard$stalagmiteDamage(DamageSources sources, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        return sources.stalagmite().directBlock(level, pos);
    }
}
