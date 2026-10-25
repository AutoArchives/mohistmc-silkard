package com.mohistmc.silkard.mixin.vanilla.commands;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.bukkit.DelegatePermissionSet;
import com.mohistmc.silkard.injected.commands.ContextCommandSourceStack;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.util.TaskChainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/5/13 22:54
 */
@Mixin(CommandSourceStack.class)
public class MixinCommandSourceStack implements ContextCommandSourceStack {

    // @formatter:off
    @Mutable @Shadow @Final public CommandSource source;
    // @formatter:on
    // CraftBukkit start
    private PermissionSet bukkitPermissions;
    public volatile CommandNode currentCommand;
    // CraftBukkit end

    @Inject(method = "<init>(Lnet/minecraft/commands/CommandSource;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec2;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/permissions/PermissionSet;Lnet/minecraft/commands/CommandSourceStack$NamesProvider;Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/entity/Entity;ZLnet/minecraft/commands/CommandResultCallback;Lnet/minecraft/commands/arguments/EntityAnchorArgument$Anchor;Lnet/minecraft/commands/CommandSigningContext;Lnet/minecraft/util/TaskChainer;)V", at = @At("RETURN"))
    private void silkard$init(CommandSource source, Vec3 position, Vec2 rotation, ServerLevel level, PermissionSet permissions, CommandSourceStack.NamesProvider namesProvider, MinecraftServer server, Entity entity, boolean silent, CommandResultCallback resultCallback, EntityAnchorArgument.Anchor anchor, CommandSigningContext signingContext, TaskChainer chatMessageChainer, CallbackInfo ci) {
        this.bukkitPermissions = new DelegatePermissionSet((CommandSourceStack) (Object) this, permissions);
    }

    @ModifyReturnValue(method = "permissions", at = @At("RETURN"))
    private PermissionSet silkard$bukkitPermissions(PermissionSet original) {
        return bukkitPermissions;
    }

    @ModifyExpressionValue(method = "broadcastToAdmins", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;isOp(Lnet/minecraft/server/players/NameAndId;)Z"))
    private boolean silkard$broadcastToAdmins(boolean original, @Local(name = "player") ServerPlayer player) {
        return player.getBukkitEntity().hasPermission("minecraft.admin.command_feedback");
    }

    @Override
    public CommandSender getBukkitSender() {
        return source.getBukkitSender(((CommandSourceStack) (Object) this));
    }

    @Override
    public CommandNode<?> silkard$currentCommand() {
        return this.currentCommand;
    }

    @Override
    public void silkard$currentCommand(CommandNode<?> node) {
        this.currentCommand = node;
    }
}
