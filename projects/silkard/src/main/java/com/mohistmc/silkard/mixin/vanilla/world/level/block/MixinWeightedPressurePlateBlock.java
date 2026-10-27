package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.ContextLevel;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(WeightedPressurePlateBlock.class)
public abstract class MixinWeightedPressurePlateBlock extends BasePressurePlateBlock {

    @Shadow @Final private int maxWeight;

    protected MixinWeightedPressurePlateBlock(BlockBehaviour.Properties properties, BlockSetType type) {
        super(properties, type);
    }

    // CraftBukkit start - Call interact event when turning on a weighted pressure plate
    @Overwrite
    protected int getSignalStrength(Level level, BlockPos pos) {
        int i = 0;
        List<? extends Entity> list = level.getEntitiesOfClass(Entity.class, TOUCH_AABB.move(pos), EntitySelector.NO_SPECTATORS.and(e -> !e.isIgnoringBlockTriggers()));
        for (Entity entity : list) {
            org.bukkit.event.Cancellable cancellable;

            if (entity instanceof Player player) {
                cancellable = CraftEventFactory.callPlayerInteractEvent(player, Action.PHYSICAL, pos, null, null, null);
            } else {
                cancellable = new EntityInteractEvent(entity.getBukkitEntity(), ((ContextLevel) level).getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()));
                level.getCraftServer().getPluginManager().callEvent((EntityInteractEvent) cancellable);
            }

            // We only want to block turning the plate on if all events are cancelled
            if (!cancellable.isCancelled()) {
                i++;
            }
        }

        i = Math.min(i, this.maxWeight);
        if (i > 0) {
            float f = (float) Math.min(this.maxWeight, i) / (float) this.maxWeight;
            return Mth.ceil(f * 15.0F);
        }
        return 0;
    }
    // CraftBukkit end
}
