package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.bukkit.BukkitUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.NetherFungusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.TreeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherFungusBlock.class)
public class MixinNetherFungusBlock {

    // CraftBukkit - set TreeType before the feature lambda runs
    @Inject(method = "performBonemeal", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
    private void silkard$performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source, CallbackInfo ci) {
        Object self = this;
        if (self == Blocks.WARPED_FUNGUS) {
            BukkitUtils.setTreeType(TreeType.WARPED_FUNGUS);
        } else if (self == Blocks.CRIMSON_FUNGUS) {
            BukkitUtils.setTreeType(TreeType.CRIMSON_FUNGUS);
        }
    }
}
