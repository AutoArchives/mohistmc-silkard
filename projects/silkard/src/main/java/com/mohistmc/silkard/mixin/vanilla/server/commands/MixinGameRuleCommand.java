package com.mohistmc.silkard.mixin.vanilla.server.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.GameRuleCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(GameRuleCommand.class)
public class MixinGameRuleCommand {

    @Redirect(method = "setRule", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/CommandSourceStack;getServer()Lnet/minecraft/server/MinecraftServer;"))
    private static CommandSourceStack silkard_getLevel(CommandSourceStack stack) {
        return stack; // CraftBukkit - pass level instead of server
    }
}
