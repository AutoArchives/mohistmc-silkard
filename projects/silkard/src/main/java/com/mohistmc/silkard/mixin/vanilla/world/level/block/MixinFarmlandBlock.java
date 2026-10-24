package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(FarmlandBlock.class)
public class MixinFarmlandBlock {

    // CraftBukkit start - MoistureChangeEvent
    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean silkard$moistureChange(ServerLevel level, BlockPos pos, BlockState state, int flags) {
        return CraftEventFactory.handleMoistureChangeEvent(level, pos, state, flags);
    }

    // CraftBukkit - EntityChangeBlockEvent when turning to dirt
    @Redirect(method = "turnToBaseBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$turnToDirt(Level level, BlockPos pos, BlockState state, Entity sourceEntity) {
        if (sourceEntity == null) {
            return level.setBlockAndUpdate(pos, state);
        }
        return CraftEventFactory.callEntityChangeBlockEvent(sourceEntity, pos, state) && level.setBlockAndUpdate(pos, state);
    }

    // CraftBukkit start - PlayerInteractEvent/EntityInteractEvent on trample
    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    private void silkard$trampleEvents(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
        if (!(level instanceof ServerLevel serverLevel) || !(level.getRandom().nextFloat() < fallDistance - 0.5) || !(entity instanceof net.minecraft.world.entity.LivingEntity)) {
            return;
        }

        boolean canTrample = entity instanceof net.minecraft.world.entity.player.Player player
                ? !level.getServer().isUnderSpawnProtection(serverLevel, pos, player)
                : serverLevel.getGameRules().get(net.minecraft.world.level.gamerules.GameRules.MOB_GRIEFING);
        // CraftBukkit - Interact soil
        org.bukkit.event.Cancellable cancellable;
        if (entity instanceof net.minecraft.world.entity.player.Player player) {
            cancellable = CraftEventFactory.callPlayerInteractEvent(player, org.bukkit.event.block.Action.PHYSICAL, pos, null, null, null);
        } else {
            EntityInteractEvent interactEvent = new EntityInteractEvent(entity.getBukkitEntity(), level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()));
            level.getCraftServer().getPluginManager().callEvent(interactEvent);
            cancellable = interactEvent;
        }
        if (cancellable.isCancelled()) {
            ci.cancel();
            return;
        }
        if (!CraftEventFactory.callEntityChangeBlockEvent(entity, pos, net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState())) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
        if (canTrample && entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > 0.512F) {
            ((FarmlandBlock) (Object) this).turnToBaseBlock(entity, state, level, pos);
            // vanilla 的 turnToBaseBlock 已执行, 跳过 vanilla fallOn 剩余部分（仅 super 空实现）
            ci.cancel();
        }
    }
    // CraftBukkit end
}
