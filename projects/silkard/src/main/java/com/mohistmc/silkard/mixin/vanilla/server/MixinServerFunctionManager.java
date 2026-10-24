package com.mohistmc.silkard.mixin.vanilla.server;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerFunctionManager.class)
public class MixinServerFunctionManager {

    @Final
    @Shadow
    private MinecraftServer server;

    @Redirect(method = "getDispatcher", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;getDispatcher()Lcom/mojang/brigadier/CommandDispatcher;"))
    private com.mojang.brigadier.CommandDispatcher<CommandSourceStack> silkard_getDispatcher(net.minecraft.commands.Commands commands) {
        return this.server.getCommands().getDispatcher();
    }
}
