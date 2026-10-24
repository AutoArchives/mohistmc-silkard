package com.mohistmc.silkard.mixin.vanilla.network.chat;

import com.mohistmc.silkard.injected.network.chat.ContextTextColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(TextColor.class)
public class MixinTextColor implements ContextTextColor {

    // CB 自增字段，26.3 vanilla 无此成员
    @Unique
    public ChatFormatting format;

    @Override
    public ChatFormatting silkard$format() {
        return format;
    }

    @Override
    public void silkard$setFormat(ChatFormatting format) {
        // Backed by a final field in the nms-patch; setting is handled at construction time
    }
}
