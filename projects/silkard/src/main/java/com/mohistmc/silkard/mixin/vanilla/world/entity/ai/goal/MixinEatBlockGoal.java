package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(EatBlockGoal.class)
public class MixinEatBlockGoal {

    @Shadow
    private Mob mob;

    @Shadow
    private Level level;

    // CraftBukkit start - EntityChangeBlockEvent
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean silkard$eatGrass(Level world, BlockPos pos, boolean drop) {
        boolean mobGriefing = ((ServerLevel) this.level).getGameRules().get(net.minecraft.world.level.gamerules.GameRules.MOB_GRIEFING);
        return CraftEventFactory.callEntityChangeBlockEvent(this.mob, pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), !mobGriefing) && world.destroyBlock(pos, drop);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean silkard$eatGrassBelow(Level world, BlockPos pos, BlockState state, int flags) {
        boolean mobGriefing = ((ServerLevel) this.level).getGameRules().get(net.minecraft.world.level.gamerules.GameRules.MOB_GRIEFING);
        return CraftEventFactory.callEntityChangeBlockEvent(this.mob, pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), !mobGriefing) && world.setBlock(pos, state, flags);
    }
    // CraftBukkit end
}
