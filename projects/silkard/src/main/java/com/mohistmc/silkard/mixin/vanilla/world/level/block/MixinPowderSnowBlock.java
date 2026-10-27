package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.gamerules.GameRules;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PowderSnowBlock.class)
public class MixinPowderSnowBlock {

    /**
     * @author Mgazul
     * @reason CraftBukkit - EntityChangeBlockEvent before the fire extinguishes the powder snow
     */
    @Overwrite
    private static void lambda$entityInside$0(Level level, BlockPos pos, Entity entity) {
        if (level instanceof ServerLevel serverlevel && entity.isOnFire() && entity.mayInteract(serverlevel, pos)) {
            // CraftBukkit start
            if (!CraftEventFactory.callEntityChangeBlockEvent(entity, pos, Blocks.AIR.defaultBlockState(), !((Boolean) serverlevel.getGameRules().get(GameRules.MOB_GRIEFING) || entity instanceof Player))) {
                return;
            }
            // CraftBukkit end
            level.destroyBlock(pos, false);
        }
    }
}
