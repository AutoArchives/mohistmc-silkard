package com.mohistmc.silkard.mixin.vanilla.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.DifficultyCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(DifficultyCommand.class)
public class MixinDifficultyCommand {

    @Shadow
    @Final
    private static com.mojang.brigadier.exceptions.DynamicCommandExceptionType ERROR_ALREADY_SAME_DIFFICULTY;

    /**
     * @author Mgazul
     * @reason CraftBukkit - per-world difficulty
     */
    @Overwrite
    public static int setDifficulty(CommandSourceStack source, Difficulty difficulty) throws CommandSyntaxException {
        ServerLevel worldserver = source.getLevel(); // CraftBukkit
        if (worldserver.getDifficulty() == difficulty) {
            throw ERROR_ALREADY_SAME_DIFFICULTY.create(difficulty.getDisplayName());
        }

        ((com.mohistmc.silkard.injected.server.level.ContextServerLevel) worldserver).silkard$serverLevelDataCB().setDifficulty(difficulty); // CraftBukkit - per-world data
        source.sendSuccess(() -> net.minecraft.network.chat.Component.translatable("commands.difficulty.success", difficulty.getDisplayName()), true);
        return 0;
    }
}
