package com.mohistmc.silkard.mixin.vanilla.network;

import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(FriendlyByteBuf.class)
public class MixinFriendlyByteBuf {

    // CraftBukkit - NbtIo.writeAnyTag 的 catch 由 IOException 放宽为 Exception；26.3 实际宿主方法为 writeNbt
    // TODO 26.3: 原 @Inject("writeNbt") 空实现已移除——宿主返回 FriendlyByteBuf 非 void,
    //  CallbackInfo 签名导致运行时 InvalidInjectionException; catch 放宽需 @WrapOperation(NbtIo.writeAnyTag) 包裹式重抛 EncoderException, 待移植
}
