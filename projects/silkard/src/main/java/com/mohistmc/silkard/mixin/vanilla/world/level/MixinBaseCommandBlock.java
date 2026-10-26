package com.mohistmc.silkard.mixin.vanilla.world.level;

import com.mohistmc.silkard.injected.commands.ContextCommands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.BaseCommandBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BaseCommandBlock.class)
public abstract class MixinBaseCommandBlock {

    @Redirect(method = "performCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V"))
    private void silkard$dispatchServerCommand(Commands commands, CommandSourceStack sender, String command) {
        // CraftBukkit - ServerCommandEvent, blocked commands (stop/kick/op/...) and command-block-overrides
        ((ContextCommands) commands).dispatchServerCommand(sender, command);
    }
}
