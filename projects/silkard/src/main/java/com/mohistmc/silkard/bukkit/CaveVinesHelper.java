package com.mohistmc.silkard.bukkit;

import java.util.LinkedList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerHarvestBlockEvent;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
public final class CaveVinesHelper {

    private CaveVinesHelper() {
    }

    // CraftBukkit start - block change + harvest events
    public static InteractionResult use(Entity sourceEntity, BlockState state, Level level, BlockPos pos) {
        if (!state.getValue(CaveVines.BERRIES)) {
            return InteractionResult.PASS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }
        if (!CraftEventFactory.callEntityChangeBlockEvent(sourceEntity, pos, state.setValue(CaveVines.BERRIES, false))) {
            return InteractionResult.SUCCESS;
        }
        if (sourceEntity instanceof Player player) {
            List<ItemStack> dropped = new LinkedList<>();
            Block.dropFromBlockInteractLootTable(serverLevel, BuiltInLootTables.HARVEST_CAVE_VINE, pos, state, level.getBlockEntity(pos), null, sourceEntity, (sl, stack) -> dropped.add(stack));
            PlayerHarvestBlockEvent event = CraftEventFactory.callPlayerHarvestBlockEvent(level, pos, player, InteractionHand.MAIN_HAND, dropped);
            if (event.isCancelled()) {
                return InteractionResult.SUCCESS;
            }
            for (org.bukkit.inventory.ItemStack stack : event.getItemsHarvested()) {
                Block.popResource(level, pos, CraftItemStack.asNMSCopy(stack));
            }
        } else {
            Block.dropFromBlockInteractLootTable(serverLevel, BuiltInLootTables.HARVEST_CAVE_VINE, pos, state, level.getBlockEntity(pos), null, sourceEntity, (sl, stack) -> Block.popResource(sl, pos, stack));
        }
        float f = Mth.randomBetween(serverLevel.getRandom(), 0.8F, 1.2F);
        serverLevel.playSound(null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, f);
        BlockState newState = state.setValue(CaveVines.BERRIES, false);
        serverLevel.setBlock(pos, newState, 2);
        serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(sourceEntity, newState));
        return InteractionResult.SUCCESS;
    }
    // CraftBukkit end
}
