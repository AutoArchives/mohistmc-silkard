package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(SweetBerryBushBlock.class)
public class MixinSweetBerryBushBlock {

    // CraftBukkit start - BlockGrowEvent
    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean silkard$berryGrowth(ServerLevel level, BlockPos pos, BlockState state, int flags) {
        if (!CraftEventFactory.handleBlockGrowEvent(level, pos, state, flags)) {
            return false;
        }
        return true;
    }

    // CraftBukkit - keep the block in the damage source so plugins get the bush location
    @Redirect(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean silkard$berryDamage(Entity instance, ServerLevel serverLevel, DamageSource source, float amount, @Local(argsOnly = true) BlockPos pos) {
        return instance.hurtServer(serverLevel, serverLevel.damageSources().sweetBerryBush().directBlock(serverLevel, pos), amount);
    }
    // CraftBukkit end

    /**
     * @author Mgazul
     * @reason CraftBukkit - PlayerHarvestBlockEvent
     */
    @Overwrite
    protected InteractionResult useWithoutItem(BlockState state, net.minecraft.world.level.Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(SweetBerryBushBlock.AGE) > 1) {
            if (level instanceof ServerLevel serverLevel) {
                // CraftBukkit start - useWithoutItem is always MAIN_HAND
                List<net.minecraft.world.item.ItemStack> dropped = new LinkedList<>();
                net.minecraft.world.level.block.Block.dropFromBlockInteractLootTable(
                        serverLevel,
                        net.minecraft.world.level.storage.loot.BuiltInLootTables.HARVEST_SWEET_BERRY_BUSH,
                        pos,
                        state,
                        level.getBlockEntity(pos),
                        null,
                        player,
                        (serverlvl, itemStack) -> dropped.add(itemStack)
                );
                org.bukkit.event.player.PlayerHarvestBlockEvent event = CraftEventFactory.callPlayerHarvestBlockEvent(serverLevel, pos, player, InteractionHand.MAIN_HAND, dropped);
                if (event.isCancelled()) {
                    return InteractionResult.SUCCESS; // We need to return a success either way, because making it PASS or FAIL will result in a bug where cancelling while harvesting w/ block in hand places block
                }
                for (org.bukkit.inventory.ItemStack itemStack : event.getItemsHarvested()) {
                    net.minecraft.world.level.block.Block.popResource(serverLevel, pos, CraftItemStack.asNMSCopy(itemStack));
                }
                // CraftBukkit end
                serverLevel.playSound(
                        null, pos, net.minecraft.sounds.SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 0.8F + serverLevel.getRandom().nextFloat() * 0.4F
                );
                BlockState newState = state.setValue(SweetBerryBushBlock.AGE, 1);
                serverLevel.setBlock(pos, newState, 2);
                serverLevel.gameEvent(net.minecraft.world.level.gameevent.GameEvent.BLOCK_CHANGE, pos, net.minecraft.world.level.gameevent.GameEvent.Context.of(player, newState));
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS; // BlockBehaviour.useWithoutItem 默认实现 (mixin 无 super)
        }
    }
}
