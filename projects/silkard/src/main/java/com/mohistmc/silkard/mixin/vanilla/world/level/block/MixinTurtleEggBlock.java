package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(TurtleEggBlock.class)
public class MixinTurtleEggBlock {

    /**
     * @author Mgazul
     * @reason CraftBukkit - PlayerInteractEvent/EntityInteractEvent when stepping on eggs
     */
    @Overwrite
    private void destroyEgg(Level level, BlockState state, BlockPos pos, Entity entity, int randomness) {
        if (state.is(net.minecraft.world.level.block.Blocks.TURTLE_EGG)
                && level instanceof ServerLevel serverLevel
                && this.canDestroyEgg(serverLevel, pos, entity)
                && level.getRandom().nextInt(randomness) == 0) {
            // CraftBukkit start - Step on eggs
            org.bukkit.event.Cancellable cancellable;
            if (entity instanceof net.minecraft.world.entity.player.Player player) {
                cancellable = CraftEventFactory.callPlayerInteractEvent(player, org.bukkit.event.block.Action.PHYSICAL, pos, null, null, null);
            } else {
                EntityInteractEvent interactEvent = new EntityInteractEvent(entity.getBukkitEntity(), CraftBlock.at(serverLevel, pos));
                serverLevel.getCraftServer().getPluginManager().callEvent(interactEvent);
                cancellable = interactEvent;
            }

            if (cancellable.isCancelled()) {
                return;
            }
            // CraftBukkit end
            this.decreaseEggs(serverLevel, pos, state);
        }
    }

    @Shadow
    private boolean canDestroyEgg(ServerLevel level, BlockPos pos, Entity entity) {
        throw new AssertionError();
    }

    @Shadow
    private void decreaseEggs(Level level, BlockPos pos, BlockState state) {
        throw new AssertionError();
    }
}
