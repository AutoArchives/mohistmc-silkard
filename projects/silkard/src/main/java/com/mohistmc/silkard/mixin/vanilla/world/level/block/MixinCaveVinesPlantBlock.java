package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.bukkit.CaveVinesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CaveVinesPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(CaveVinesPlantBlock.class)
public class MixinCaveVinesPlantBlock {

    // CraftBukkit start
    @Redirect(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CaveVines;use(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/InteractionResult;"))
    private static InteractionResult silkard$use(Entity sourceEntity, BlockState state, Level level, BlockPos pos) {
        return CaveVinesHelper.use(sourceEntity, state, level, pos);
    }
    // CraftBukkit end
}
