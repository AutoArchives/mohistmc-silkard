package com.mohistmc.silkard.mixin.vanilla.world.level;

import com.mohistmc.silkard.injected.world.level.ContextBaseCommandBlock;
import com.mohistmc.silkard.injected.world.level.ContextCloseableCommandBlockSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.BaseCommandBlock;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(targets = "net.minecraft.world.level.BaseCommandBlock$CloseableCommandBlockSource")
public class MixinBaseCommandBlock$CloseableCommandBlockSource implements ContextCloseableCommandBlockSource {

    @Shadow
    @Final
    private BaseCommandBlock this$0;

    @Shadow
    private boolean closed;

    @Override
    public void silkard$setClosed(boolean closed) {
        this.closed = closed;
    }

    public CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return ((ContextBaseCommandBlock) (Object) this.this$0).getBukkitSender(wrapper);
    }
}
