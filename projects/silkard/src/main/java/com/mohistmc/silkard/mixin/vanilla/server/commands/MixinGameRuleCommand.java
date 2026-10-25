package com.mohistmc.silkard.mixin.vanilla.server.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.GameRuleCommand;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(GameRuleCommand.class)
public class MixinGameRuleCommand {
    // CraftBukkit - per-world game rules: pending per-world GameRules wiring
}
