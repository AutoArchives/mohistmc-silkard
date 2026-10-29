package com.mohistmc.silkard.mixin.vanilla.server.gui;

import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.server.gui.MinecraftServerGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(MinecraftServerGui.class)
public class MixinMinecraftServerGui {

    // CraftBukkit start
    @Unique
    private static final Pattern silkard$ansi = Pattern.compile("\\x1B\\[([0-9]{1,2}(;[0-9]{1,2})*)?[m|K]");

    @Redirect(method = "print", at = @At(value = "INVOKE", target = "Ljavax/swing/text/Document;insertString(ILjava/lang/String;Ljavax/swing/text/AttributeSet;)V"))
    private void silkard$stripAnsi(Document document, int offset, String text, AttributeSet attributes) throws BadLocationException {
        document.insertString(offset, MixinMinecraftServerGui.silkard$ansi.matcher(text).replaceAll(""), attributes);
    }
    // CraftBukkit end
}
