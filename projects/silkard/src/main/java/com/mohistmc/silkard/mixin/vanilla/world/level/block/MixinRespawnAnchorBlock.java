package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.server.level.ContextServerPlayer;
import com.mohistmc.silkard.injected.world.damagesource.ContextDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.player.PlayerSpawnChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(RespawnAnchorBlock.class)
public class MixinRespawnAnchorBlock {

    @Unique
    private org.bukkit.block.BlockState silkard$capturedState;

    @Redirect(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setRespawnPosition(Lnet/minecraft/server/level/ServerPlayer$RespawnConfig;Z)V"))
    private void silkard$setSpawn(ServerPlayer player, ServerPlayer.RespawnConfig config, boolean showMessage) {
        ((ContextServerPlayer) player).setRespawnPosition(config, showMessage, PlayerSpawnChangeEvent.Cause.RESPAWN_ANCHOR);
    }

    // capture BlockState before remove block
    @Inject(method = "explode", at = @At("HEAD"))
    private void silkard$captureState(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
        this.silkard$capturedState = CraftBlock.at(level, pos).getState();
    }

    @Redirect(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;badRespawnPointExplosion(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource silkard$badRespawn(DamageSources sources, Vec3 vec3) {
        return ((ContextDamageSources) sources).badRespawnPointExplosion(vec3, this.silkard$capturedState);
    }
}
