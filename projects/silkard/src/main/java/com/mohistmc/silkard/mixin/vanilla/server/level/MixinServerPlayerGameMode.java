package com.mohistmc.silkard.mixin.vanilla.server.level;

import com.mohistmc.silkard.injected.server.level.ContextServerPlayerGameMode;
import java.util.ArrayList;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockBreakEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerGameMode implements ContextServerPlayerGameMode {

    @Shadow
    private ServerLevel level;

    @Shadow
    @Final
    private ServerPlayer player;

    @Shadow
    private GameType gameModeForPlayer;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    public boolean isCreative() {
        throw new AssertionError();
    }

    // CB 自增字段，26.3 vanilla 无此成员
    @Unique
    public boolean firedInteract;

    @Unique
    public boolean interactResult;

    @Unique
    public BlockPos interactPosition;

    @Unique
    public InteractionHand interactHand;

    @Unique
    public ItemStack interactItemStack;

    @Override
    public boolean silkard$isFiredInteract() {
        return firedInteract;
    }

    @Override
    public void silkard$setFiredInteract(boolean firedInteract) {
        this.firedInteract = firedInteract;
    }

    @Override
    public boolean silkard$getInteractResult() {
        return interactResult;
    }

    @Override
    public BlockPos silkard$getinteractPosition() {
        return interactPosition;
    }

    @Override
    public InteractionHand silkard$getinteractHand() {
        return interactHand;
    }

    @Override
    public ItemStack silkard$getinteractItemStack() {
        return interactItemStack;
    }

    // CraftBukkit start - BlockBreakEvent
    @Overwrite
    public boolean destroyBlock(BlockPos pos) {
        return this.destroyBlock(pos, -1);
    }

    @Unique
    public boolean destroyBlock(BlockPos pos, int ack) {
        BlockState blockstate = this.level.getBlockState(pos);
        org.bukkit.block.Block bblock = CraftBlock.at(this.level, pos);

        boolean isSwordNoBreak = !this.player.getMainHandItem().canDestroyBlock(blockstate, this.level, pos, this.player);

        if (this.level.getBlockEntity(pos) == null && !isSwordNoBreak) {
            this.player.connection.send(new ClientboundBlockUpdatePacket(pos, Blocks.AIR.defaultBlockState()));
        }

        BlockBreakEvent event = new BlockBreakEvent(bblock, this.player.getBukkitEntity());
        event.setCancelled(isSwordNoBreak);

        // Calculate default block experience
        BlockState nmsData = this.level.getBlockState(pos);
        Block nmsBlock = nmsData.getBlock();
        ItemStack itemstack = this.player.getItemBySlot(EquipmentSlot.MAINHAND);

        if (nmsBlock != null && !event.isCancelled() && !this.isCreative() && this.player.hasCorrectToolForDrops(nmsBlock.defaultBlockState())) {
            event.setExpToDrop(nmsBlock.getExpDrop(nmsData, this.level, pos, itemstack, true));
        }

        this.level.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            if (isSwordNoBreak) {
                return false;
            }

            // Let the client know the block still exists
            this.player.connection.send(new ClientboundBlockUpdatePacket(this.level, pos));

            for (Direction dir : Direction.values()) {
                this.player.connection.send(new ClientboundBlockUpdatePacket(this.level, pos.relative(dir)));
            }

            // SPIGOT-8042: need to ACK above changes first
            this.player.connection.send(new ClientboundBlockChangedAckPacket(ack));
            BlockEntity tileentity = this.level.getBlockEntity(pos);
            if (tileentity != null) {
                this.player.connection.send(tileentity.getUpdatePacket());
            }
            return false;
        }

        blockstate = this.level.getBlockState(pos);
        if (blockstate.isAir()) {
            return false;
        }

        BlockEntity blockentity = this.level.getBlockEntity(pos);
        Block block = blockstate.getBlock();

        if (block instanceof GameMasterBlock && !this.player.canUseGameMasterBlocks()) {
            this.level.sendBlockUpdated(pos, blockstate, blockstate, 3);
            return false;
        } else if (this.player.blockActionRestricted(this.level, pos, this.gameModeForPlayer)) {
            return false;
        } else {
            org.bukkit.block.BlockState state = bblock.getState();
            this.level.silkard$setCaptureDrops(new ArrayList<ItemEntity>());
            BlockState blockstate1 = block.playerWillDestroy(this.level, pos, blockstate, this.player);
            boolean flag = this.level.removeBlock(pos, false);

            if (SharedConstants.DEBUG_BLOCK_BREAK) {
                LOGGER.info("server broke {} {} -> {}", pos, blockstate1, this.level.getBlockState(pos));
            }

            if (flag) {
                block.destroy(this.level, pos, blockstate1);
            }

            if (!this.player.preventsBlockDrops()) {
                ItemStack itemstack1 = this.player.getMainHandItem();
                ItemStack itemstack2 = itemstack1.copy();
                boolean flag1 = this.player.hasCorrectToolForDrops(blockstate1);

                itemstack1.mineBlock(this.level, blockstate1, pos, this.player);
                if (flag && flag1 && event.isDropItems()) {
                    block.playerDestroy(this.level, this.player, pos, blockstate1, blockentity, itemstack2);
                }
            }

            if (event.isDropItems()) {
                CraftEventFactory.handleBlockDropItemEvent(bblock, state, this.player, this.level.silkard$captureDrops());
            }
            this.level.silkard$setCaptureDrops(null);

            // Drop event experience
            if (flag) {
                blockstate.getBlock().popExperience(this.level, pos, event.getExpToDrop());
            }
            return true;
        }
    }
    // CraftBukkit end
}
