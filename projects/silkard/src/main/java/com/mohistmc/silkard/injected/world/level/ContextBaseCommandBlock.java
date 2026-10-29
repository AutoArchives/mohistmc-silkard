package com.mohistmc.silkard.injected.world.level;

import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;

public interface ContextBaseCommandBlock {

    default CommandSender getBukkitSender(CommandSourceStack wrapper) {
        throw new IllegalStateException("Not implemented");
    }
}
