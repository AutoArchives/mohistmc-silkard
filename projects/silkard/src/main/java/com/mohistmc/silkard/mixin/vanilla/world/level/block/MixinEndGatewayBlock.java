package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.portal.ContextTeleportTransition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.portal.TeleportTransition;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(EndGatewayBlock.class)
public class MixinEndGatewayBlock {

    @Inject(method = "getPortalDestination", at = @At("RETURN"))
    private void silkard$endGatewayCause(ServerLevel level, Entity entity, BlockPos pos, CallbackInfoReturnable<TeleportTransition> cir) {
        TeleportTransition transition = cir.getReturnValue();
        if (transition != null) {
            ((ContextTeleportTransition) (Object) transition).silkard$cause(PlayerTeleportEvent.TeleportCause.END_GATEWAY);
        }
    }
}
