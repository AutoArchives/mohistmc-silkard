package com.mohistmc.silkard.mixin.vanilla.server.commands;

import net.minecraft.server.commands.ScheduleCommand;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ScheduleCommand.class)
public class MixinScheduleCommand {
    // CraftBukkit - SPIGOT-6667: pending per-world scheduled events wiring
}
