package com.mohistmc.silkard.mixin.vanilla.network;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.handler.codec.EncoderException;
import java.io.DataOutput;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(FriendlyByteBuf.class)
public class MixinFriendlyByteBuf {

    // CraftBukkit - wrap any failure instead of only IOException
    @WrapOperation(method = "writeNbt(Lio/netty/buffer/ByteBuf;Lnet/minecraft/nbt/Tag;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtIo;writeAnyTag(Lnet/minecraft/nbt/Tag;Ljava/io/DataOutput;)V"))
    private static void silkard$writeNbt(Tag tag, DataOutput output, Operation<Void> original) {
        try {
            original.call(tag, output);
        } catch (Exception ex) {
            throw new EncoderException(ex);
        }
    }
}
