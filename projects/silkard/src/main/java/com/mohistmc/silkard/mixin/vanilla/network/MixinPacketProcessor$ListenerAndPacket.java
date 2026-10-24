package com.mohistmc.silkard.mixin.vanilla.network;

import com.mohistmc.silkard.injected.server.network.ContextServerCommonPacketListenerImpl;
import net.minecraft.network.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.3 里 CB 的守卫点在包私有嵌套 record PacketProcessor$ListenerAndPacket.handle() 内。
 */
@Mixin(targets = "net.minecraft.network.PacketProcessor$ListenerAndPacket")
public abstract class MixinPacketProcessor$ListenerAndPacket {

    @Shadow
    public abstract PacketListener listener();

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void silkard$handle(CallbackInfo ci) {
        if (this.listener() instanceof ContextServerCommonPacketListenerImpl server
                && server.silkard$processedDisconnect()) {
            ci.cancel(); // CraftBukkit - Don't handle sync packets for kicked players
        }
    }
}
