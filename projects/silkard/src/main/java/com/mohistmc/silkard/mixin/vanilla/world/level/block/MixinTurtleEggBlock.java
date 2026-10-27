package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.ContextLevelWriter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityInteractEvent;
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
        if (state.is(Blocks.TURTLE_EGG)
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

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockGrowEvent on crack, BlockFadeEvent on hatch, SpawnReason.EGG
     */
    @Overwrite
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.shouldUpdateHatchLevel(level, pos) && TurtleEggBlock.onSand(level, pos)) {
            int hatch = state.getValue(TurtleEggBlock.HATCH);
            if (hatch < TurtleEggBlock.MAX_HATCH_LEVEL) {
                // CraftBukkit start - Call BlockGrowEvent
                if (!CraftEventFactory.handleBlockGrowEvent(level, pos, state.setValue(TurtleEggBlock.HATCH, hatch + 1), 2)) {
                    return;
                }
                // CraftBukkit end
                level.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
            } else {
                // CraftBukkit start - Call BlockFadeEvent
                if (CraftEventFactory.callBlockFadeEvent(level, pos, Blocks.AIR.defaultBlockState()).isCancelled()) {
                    return;
                }
                // CraftBukkit end
                level.playSound(null, pos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                level.removeBlock(pos, false);
                level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));

                for (int i = 0; i < state.getValue(TurtleEggBlock.EGGS); i++) {
                    level.levelEvent(2001, pos, net.minecraft.world.level.block.Block.getId(state));
                    Turtle turtle = EntityTypes.TURTLE.create(level, EntitySpawnReason.BREEDING);
                    if (turtle != null) {
                        turtle.setAge(-24000);
                        turtle.setHomePos(pos);
                        turtle.snapTo(pos.getX() + 0.3 + i * 0.2, pos.getY(), pos.getZ() + 0.3, 0.0F, 0.0F);
                        ((ContextLevelWriter) level).addFreshEntity(turtle, CreatureSpawnEvent.SpawnReason.EGG); // CraftBukkit
                    }
                }
            }
        }
    }

    @Shadow
    private boolean shouldUpdateHatchLevel(Level level, BlockPos pos) {
        throw new AssertionError();
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
