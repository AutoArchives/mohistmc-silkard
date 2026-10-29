package com.mohistmc.silkard.mixin.vanilla.server.level;

import com.mohistmc.silkard.injected.server.level.ContextServerPlayerGameMode;
import java.util.ArrayList;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockBreakEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(value = ServerPlayerGameMode.class, priority = 900) // applied after Fabric's injectors
public class MixinServerPlayerGameMode implements ContextServerPlayerGameMode {

    @Shadow
    private ServerLevel level;

    @Shadow
    @Final
    private ServerPlayer player;

    @Shadow
    private GameType gameModeForPlayer;

    @Shadow
    public boolean isCreative() {
        throw new AssertionError();
    }

    @Unique
    private BlockBreakEvent silkard$breakEvent;

    @Unique
    private org.bukkit.block.Block silkard$breakBukkitBlock;

    @Unique
    private org.bukkit.block.BlockState silkard$breakBukkitState;

    @Unique
    private BlockState silkard$breakNmsState;

    @Unique
    private int silkard$breakAck = -1;

    @Unique
    private boolean silkard$breakActive;

    @Unique
    private boolean silkard$breakRemoved;

    @Unique
    private boolean silkard$breakPreventsDrops;

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
    @Inject(method = "destroyAndAck", at = @At("HEAD"))
    private void silkard$captureBreakAck(BlockPos pos, int sequence, String exitId, CallbackInfo ci) {
        this.silkard$breakAck = sequence;
    }

    @Inject(method = "destroyAndAck", at = @At("RETURN"))
    private void silkard$resetBreakAck(BlockPos pos, int sequence, String exitId, CallbackInfo ci) {
        this.silkard$breakAck = -1;
    }

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void silkard$fireBlockBreakEvent(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        this.silkard$breakActive = false;
        this.silkard$breakRemoved = false;
        this.silkard$breakPreventsDrops = false;
        this.silkard$breakEvent = null;
        this.silkard$breakBukkitBlock = null;
        this.silkard$breakBukkitState = null;

        BlockState blockstate = this.level.getBlockState(pos);
        org.bukkit.block.Block bblock = CraftBlock.at(this.level, pos);
        boolean isSwordNoBreak = !this.player.getMainHandItem().canDestroyBlock(blockstate, this.level, pos, this.player);

        if (this.level.getBlockEntity(pos) == null && !isSwordNoBreak) {
            this.player.connection.send(new ClientboundBlockUpdatePacket(pos, Blocks.AIR.defaultBlockState()));
        }

        BlockBreakEvent event = new BlockBreakEvent(bblock, this.player.getBukkitEntity());
        event.setCancelled(isSwordNoBreak);

        BlockState nmsData = this.level.getBlockState(pos);
        Block nmsBlock = nmsData.getBlock();
        ItemStack itemstack = this.player.getItemBySlot(EquipmentSlot.MAINHAND);

        if (nmsBlock != null && !event.isCancelled() && !this.isCreative() && this.player.hasCorrectToolForDrops(nmsBlock.defaultBlockState())) {
            event.setExpToDrop(nmsBlock.getExpDrop(nmsData, this.level, pos, itemstack, true));
        }

        this.level.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            if (!isSwordNoBreak) {
                this.player.connection.send(new ClientboundBlockUpdatePacket(this.level, pos));
                for (Direction dir : Direction.values()) {
                    this.player.connection.send(new ClientboundBlockUpdatePacket(this.level, pos.relative(dir)));
                }
                this.player.connection.send(new ClientboundBlockChangedAckPacket(this.silkard$breakAck));
                BlockEntity blockentity = this.level.getBlockEntity(pos);
                if (blockentity != null) {
                    this.player.connection.send(blockentity.getUpdatePacket());
                }
            }
            cir.setReturnValue(false);
            return;
        }

        BlockState current = this.level.getBlockState(pos);
        if (current.isAir()) {
            // A plugin removed the block without cancelling
            cir.setReturnValue(false);
            return;
        }

        this.silkard$breakEvent = event;
        this.silkard$breakBukkitBlock = bblock;
        this.silkard$breakNmsState = current;
    }

    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private void silkard$captureDrops(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        this.silkard$breakBukkitState = this.silkard$breakBukkitBlock.getState();
        this.silkard$breakActive = true;
        this.level.silkard$setCaptureDrops(new ArrayList<>());
    }

    @Redirect(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;canDestroyBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean silkard$skipCanDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player) {
        // The event already handled the sword and creative pre-cancel
        return true;
    }

    @Redirect(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean silkard$removeBlock(ServerLevel level, BlockPos pos, boolean moving) {
        boolean removed = level.removeBlock(pos, moving);
        this.silkard$breakRemoved = removed;
        return removed;
    }

    @Redirect(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;preventsBlockDrops()Z"))
    private boolean silkard$skipPreventsBlockDrops(ServerPlayer player) {
        this.silkard$breakPreventsDrops = player.preventsBlockDrops();
        // Keep running so the drop event still fires
        return false;
    }

    @Redirect(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;mineBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V"))
    private void silkard$mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, Player player) {
        if (!this.silkard$breakPreventsDrops) {
            stack.mineBlock(level, state, pos, player);
        }
    }

    @Redirect(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;playerDestroy(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemStack;)V"))
    private void silkard$playerDestroy(Block block, ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
        if (!this.silkard$breakPreventsDrops && (this.silkard$breakEvent == null || this.silkard$breakEvent.isDropItems())) {
            block.playerDestroy(level, player, pos, state, blockEntity, tool);
        }
    }

    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void silkard$afterDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!this.silkard$breakActive) {
            return;
        }
        this.silkard$breakActive = false;

        BlockBreakEvent event = this.silkard$breakEvent;
        if (event == null || event.isDropItems()) {
            CraftEventFactory.handleBlockDropItemEvent(this.silkard$breakBukkitBlock, this.silkard$breakBukkitState, this.player, this.level.silkard$captureDrops());
        }
        this.level.silkard$setCaptureDrops(null);

        if (this.silkard$breakRemoved && event != null && this.silkard$breakNmsState != null) {
            this.silkard$breakNmsState.getBlock().popExperience(this.level, pos, event.getExpToDrop());
        }
    }
    // CraftBukkit end
}
