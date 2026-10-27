package com.mohistmc.silkard.mixin.vanilla.commands;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mohistmc.silkard.injected.commands.ContextCommands;
import com.mohistmc.silkard.injected.server.ContextMinecraftServer;
import com.mohistmc.silkard.injected.server.level.ContextServerLevel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.command.BukkitCommandWrapper;
import org.bukkit.craftbukkit.command.CraftCommandMap;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(Commands.class)
public abstract class MixinCommands implements ContextCommands {

    @Shadow
    @Final
    private CommandDispatcher<CommandSourceStack> dispatcher;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    private static ClientboundCommandsPacket.NodeInspector<CommandSourceStack> COMMAND_NODE_INSPECTOR;

    // CraftBukkit start
    @Override
    public void dispatchServerCommand(CommandSourceStack sender, String command) {
        Joiner joiner = Joiner.on(" ");
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        ServerCommandEvent event = new ServerCommandEvent(sender.getBukkitSender(), command);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        command = event.getCommand();

        String[] args = command.split(" ");

        String cmd = args[0];
        if (cmd.startsWith("minecraft:")) cmd = cmd.substring("minecraft:".length());
        if (cmd.startsWith("bukkit:")) cmd = cmd.substring("bukkit:".length());

        // Block disallowed commands
        if (cmd.equalsIgnoreCase("stop") || cmd.equalsIgnoreCase("kick") || cmd.equalsIgnoreCase("op")
                || cmd.equalsIgnoreCase("deop") || cmd.equalsIgnoreCase("ban") || cmd.equalsIgnoreCase("ban-ip")
                || cmd.equalsIgnoreCase("pardon") || cmd.equalsIgnoreCase("pardon-ip") || cmd.equalsIgnoreCase("reload")) {
            return;
        }

        // Handle vanilla commands;
        if (((ContextServerLevel) sender.getLevel()).getCraftServer().getCommandBlockOverride(args[0])) {
            args[0] = "minecraft:" + args[0];
        }

        String newCommand = joiner.join(args);
        this.performPrefixedCommand(sender, newCommand, newCommand);
    }
    // CraftBukkit end

    /**
     * @author Mgazul
     * @reason CraftBukkit - delegate to label overload
     */
    @Overwrite
    public void performPrefixedCommand(CommandSourceStack sender, String command) {
        // CraftBukkit start
        this.performPrefixedCommand(sender, command, command);
    }

    @Override
    public void performPrefixedCommand(CommandSourceStack sender, String command, String label) {
        command = Commands.trimOptionalPrefix(command);
        this.performCommand(this.dispatcher.parse(command, sender), command, label);
        // CraftBukkit end
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - delegate to label overload
     */
    @Overwrite
    public void performCommand(ParseResults<CommandSourceStack> command, String commandString) {
        this.performCommand(command, commandString, commandString);
    }

    @Override
    public void performCommand(ParseResults<CommandSourceStack> command, String commandString, String label) {
        CommandSourceStack commandsourcestack = (CommandSourceStack) command.getContext().getSource();

        Profiler.get().push(() -> {
            return "/" + commandString;
        });
        // CraftBukkit start
        ContextChain<CommandSourceStack> contextchain = silkard$finishParsing(command, commandString, commandsourcestack, label);
        // CraftBukkit end

        try {
            if (contextchain != null) {
                Commands.executeCommandInContext(commandsourcestack, (executioncontext) -> {
                    ExecutionContext.queueInitialCommandExecution(executioncontext, commandString, contextchain, commandsourcestack, CommandResultCallback.EMPTY);
                });
            }
        } catch (Exception exception) {
            MutableComponent mutablecomponent = Component.literal(exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("Command exception: /{}", commandString, exception);
                StackTraceElement[] astacktraceelement = exception.getStackTrace();

                for (int i = 0; i < Math.min(astacktraceelement.length, 3); ++i) {
                    mutablecomponent.append("\n\n").append(astacktraceelement[i].getMethodName()).append("\n ").append(astacktraceelement[i].getFileName()).append(":").append(String.valueOf(astacktraceelement[i].getLineNumber()));
                }
            }

            commandsourcestack.sendFailure(Component.translatable("command.failed").withStyle((chatmodifier) -> {
                return chatmodifier.withHoverEvent(new HoverEvent.ShowText(mutablecomponent));
            }));
            if (SharedConstants.DEBUG_VERBOSE_COMMAND_ERRORS || SharedConstants.IS_RUNNING_IN_IDE) {
                commandsourcestack.sendFailure(Component.literal(Util.describeError(exception)));
                LOGGER.error("'/{}' threw an exception", commandString, exception);
            }
        } finally {
            Profiler.get().pop();
        }
    }

    // CraftBukkit start
    @Unique
    private static @Nullable ContextChain<CommandSourceStack> silkard$finishParsing(ParseResults<CommandSourceStack> command, String commandString, CommandSourceStack sender, String label) {
        try {
            Commands.validateParseResults(command);
            return (ContextChain<CommandSourceStack>) ContextChain.tryFlatten(command.getContext().build(commandString)).orElseThrow(() -> {
                return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(command.getReader());
            });
        } catch (CommandSyntaxException commandsyntaxexception) {
            sender.sendFailure(ComponentUtils.fromMessage(commandsyntaxexception.getRawMessage()));
            if (commandsyntaxexception.getInput() != null && commandsyntaxexception.getCursor() >= 0) {
                int i = Math.min(commandsyntaxexception.getInput().length(), commandsyntaxexception.getCursor());
                MutableComponent mutablecomponent = Component.empty().withStyle(ChatFormatting.GRAY).withStyle((style) -> {
                    return style.withClickEvent(new ClickEvent.SuggestCommand("/" + label)); // CraftBukkit - use label
                });

                if (i > 10) {
                    mutablecomponent.append(CommonComponents.ELLIPSIS);
                }

                mutablecomponent.append(commandsyntaxexception.getInput().substring(Math.max(0, i - 10), i));
                if (i < commandsyntaxexception.getInput().length()) {
                    Component component = Component.literal(commandsyntaxexception.getInput().substring(i)).withStyle(ChatFormatting.RED, ChatFormatting.UNDERLINE);

                    mutablecomponent.append(component);
                }

                mutablecomponent.append(Component.translatable("command.context.here").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
                sender.sendFailure(mutablecomponent);
            }

            return null;
        }
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - PlayerCommandSendEvent + vanilla command merge
     */
    @Overwrite
    public void sendCommands(ServerPlayer player) {
        // CraftBukkit start
        if (org.spigotmc.SpigotConfig.tabComplete < 0) return; // Spigot
        // Register Vanilla commands into builtRoot as before
        Map<CommandNode<CommandSourceStack>, CommandNode<CommandSourceStack>> map = new IdentityHashMap<>(); // Use identity to prevent aliasing issues
        RootCommandNode<CommandSourceStack> vanillaRoot = new RootCommandNode<>();

        RootCommandNode<CommandSourceStack> vanilla = ((ContextMinecraftServer) player.level().getServer()).vanillaCommandDispatcher().getDispatcher().getRoot();
        map.put(vanilla, vanillaRoot);
        fillUsableCommands(vanilla, vanillaRoot, player.createCommandSourceStack(), (Map) map);

        // Now build the global commands in a second pass
        RootCommandNode<CommandSourceStack> rootcommandnode = new RootCommandNode<>();

        map.put(this.dispatcher.getRoot(), rootcommandnode);
        fillUsableCommands(this.dispatcher.getRoot(), rootcommandnode, player.createCommandSourceStack(), (Map) map);

        // CraftBukkit - client parses the typed command against this tree, anything missing renders red
        CraftServer craftServer = ((ContextMinecraftServer) player.level().getServer()).silkard$server();
        if (craftServer != null) {
            for (Map.Entry<String, org.bukkit.command.Command> entry : ((CraftCommandMap) craftServer.getCommandMap()).getKnownCommands().entrySet()) {
                if (rootcommandnode.getChild(entry.getKey()) != null) {
                    continue;
                }
                CommandDispatcher<CommandSourceStack> wrapper = new CommandDispatcher<>();
                rootcommandnode.addChild(new BukkitCommandWrapper(craftServer, entry.getValue()).register(wrapper, entry.getKey()));
            }
        }

        Collection<String> bukkit = new LinkedHashSet<>();
        for (CommandNode<CommandSourceStack> node : rootcommandnode.getChildren()) {
            bukkit.add(node.getName());
        }

        PlayerCommandSendEvent event = new PlayerCommandSendEvent(player.getBukkitEntity(), new LinkedHashSet<>(bukkit));
        event.getPlayer().getServer().getPluginManager().callEvent(event);

        // Remove labels that were removed during the event; brigadier has no removeCommand, rebuild root instead
        RootCommandNode<CommandSourceStack> filteredRoot = new RootCommandNode<>();
        for (CommandNode<CommandSourceStack> node : new ArrayList<>(rootcommandnode.getChildren())) {
            if (event.getCommands().contains(node.getName())) {
                if (!org.spigotmc.SpigotConfig.sendNamespaced && node.getName().contains(":")) continue; // Spigot
                filteredRoot.addChild(node);
            }
        }
        rootcommandnode = filteredRoot;
        // CraftBukkit end
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Sending {} commands to {}", rootcommandnode.getChildren().size(), player.getPlainTextName());
        player.connection.send(new ClientboundCommandsPacket(rootcommandnode, COMMAND_NODE_INSPECTOR));
    }

    @Shadow
    private static <S> void fillUsableCommands(CommandNode<S> source, CommandNode<S> target, S commandFilter, Map<CommandNode<S>, CommandNode<S>> converted) {
        throw new AssertionError();
    }
    // CraftBukkit end
}
