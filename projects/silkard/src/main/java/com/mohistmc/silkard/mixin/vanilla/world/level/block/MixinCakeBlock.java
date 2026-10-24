package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(CakeBlock.class)
public class MixinCakeBlock {

    /**
     * @author Mgazul
     * @reason CraftBukkit - FoodLevelChangeEvent
     */
    @Overwrite
    protected static InteractionResult eat(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        }

        player.awardStat(net.minecraft.stats.Stats.EAT_CAKE_SLICE);
        // CraftBukkit start
        // player.getFoodData().eat(2, 0.1F);
        int oldFoodLevel = player.getFoodData().foodLevel;

        FoodLevelChangeEvent event = CraftEventFactory.callFoodLevelChangeEvent(player, 2 + oldFoodLevel);

        if (!event.isCancelled()) {
            player.getFoodData().eat(event.getFoodLevel() - oldFoodLevel, 0.1F);
        }

        ((net.minecraft.server.level.ServerPlayer) player).getBukkitEntity().sendHealthUpdate();
        // CraftBukkit end
        int bites = state.getValue(CakeBlock.BITES);
        level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.EAT, pos);
        if (bites < 6) {
            level.setBlockAndUpdate(pos, state.setValue(CakeBlock.BITES, bites + 1));
        } else {
            level.removeBlock(pos, false);
            level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.BLOCK_DESTROY, pos);
        }

        return InteractionResult.SUCCESS;
    }
}
