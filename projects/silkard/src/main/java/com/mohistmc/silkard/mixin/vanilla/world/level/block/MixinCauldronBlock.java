package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.bukkit.BukkitUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(CauldronBlock.class)
public class MixinCauldronBlock {

    @Inject(method = "receiveStalactiteDrip", at = @At("HEAD"), cancellable = true)
    private void silkard$receiveStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid, CallbackInfo ci) {
        if (fluid == Fluids.WATER) {
            BukkitUtils.changeCauldronLevel(level, pos, Blocks.WATER_CAULDRON.defaultBlockState(), null, CauldronLevelChangeEvent.ChangeReason.NATURAL_FILL);
            level.levelEvent(1047, pos, 0);
        } else if (fluid == Fluids.LAVA) {
            BukkitUtils.changeCauldronLevel(level, pos, Blocks.LAVA_CAULDRON.defaultBlockState(), null, CauldronLevelChangeEvent.ChangeReason.NATURAL_FILL);
            level.levelEvent(1046, pos, 0);
        }
        ci.cancel();
    }
}
