package com.mohistmc.silkard.mixin.vanilla.network.protocol.common.custom;

import com.mohistmc.silkard.injected.network.ContextDiscardedPayload;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(DiscardedPayload.class)
public class MixinDiscardedPayload implements ContextDiscardedPayload {

    @Unique
    private ByteBuf silkard$data;

    @Override
    public ByteBuf silkard$data() {
        return this.silkard$data;
    }

    @Override
    public void silkard$data(ByteBuf data) {
        this.silkard$data = data;
    }

    // CraftBukkit start - store and reserialize data
    /**
     * @author Mgazul
     * @reason CraftBukkit - keep payload bytes
     */
    @Overwrite
    public static <T extends FriendlyByteBuf> StreamCodec<T, DiscardedPayload> codec(Identifier id, int maxPayloadSize) {
        return CustomPacketPayload.codec((payload, buf) -> {
            ByteBuf data = ((ContextDiscardedPayload) (Object) payload).silkard$data();
            if (data != null) {
                buf.writeBytes(data);
            }
        }, buf -> {
            int j = buf.readableBytes();
            if (j >= 0 && j <= maxPayloadSize) {
                DiscardedPayload payload = new DiscardedPayload(id);
                ((ContextDiscardedPayload) (Object) payload).silkard$data(buf.readBytes(j));
                return payload;
            }
            throw new IllegalArgumentException("Payload may not be larger than " + maxPayloadSize + " bytes");
        });
    }
    // CraftBukkit end
}
