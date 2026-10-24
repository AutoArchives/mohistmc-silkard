package com.mohistmc.silkard.mixin.vanilla.server.network;

import com.mohistmc.silkard.injected.server.network.ContextServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl implements ContextServerGamePacketListenerImpl {

    @Redirect(method = "forceSendPlayerSupportBlocks", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"))
    private void silkard$demoteStandingOnAirLog(Logger instance, String format, Object arg) {
        // CraftBukkit - SPIGOT-8156: warn -> debug
        instance.debug(format, arg);
    }
}
