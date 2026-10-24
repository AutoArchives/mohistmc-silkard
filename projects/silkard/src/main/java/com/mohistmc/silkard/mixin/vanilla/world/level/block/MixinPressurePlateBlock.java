package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.ContextLevel;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.phys.AABB;
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
// type 是 BasePressurePlateBlock 的 protected final 字段（26.3 没有 type() 方法）→ extends 继承
@Mixin(PressurePlateBlock.class)
public abstract class MixinPressurePlateBlock extends net.minecraft.world.level.block.BasePressurePlateBlock {

    protected MixinPressurePlateBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties properties,
                                      net.minecraft.world.level.block.state.properties.BlockSetType type) {
        super(properties, type);
    }

    @Shadow
    protected abstract int getSignalForState(BlockState state);

    /**
     * @author Mgazul
     * @reason CraftBukkit - Call interact event when turning on a pressure plate
     */
    @Overwrite
    protected int getSignalStrength(Level level, BlockPos pos) {
        Class<? extends Entity> entityClass = switch (this.type.pressurePlateSensitivity()) {
            case EVERYTHING -> Entity.class;
            case MOBS -> LivingEntity.class;
        };
        // CraftBukkit start - Call interact event when turning on a pressure plate
        List<? extends Entity> list = level.getEntitiesOfClass(entityClass, TOUCH_AABB.move(pos), EntitySelector.NO_SPECTATORS.and(e -> !e.isIgnoringBlockTriggers()));
        for (Entity entity : list) {
            if (this.getSignalForState(level.getBlockState(pos)) == 0) {
                org.bukkit.World bworld = ((ContextLevel) level).getWorld();
                org.bukkit.plugin.PluginManager manager = level.getCraftServer().getPluginManager();
                org.bukkit.event.Cancellable cancellable;

                if (entity instanceof Player player) {
                    cancellable = CraftEventFactory.callPlayerInteractEvent(player, org.bukkit.event.block.Action.PHYSICAL, pos, null, null, null);
                } else {
                    cancellable = new EntityInteractEvent(entity.getBukkitEntity(), bworld.getBlockAt(pos.getX(), pos.getY(), pos.getZ()));
                    manager.callEvent((EntityInteractEvent) cancellable);
                }

                // We only want to block turning the plate on if all events are cancelled
                if (cancellable.isCancelled()) {
                    continue;
                }
            }

            return 15;
        }

        return 0;
        // CraftBukkit end
    }

    // 复制 BasePressurePlateBlock.TOUCH_AABB（protected 继承字段 shadow 不可靠）
    private static final AABB TOUCH_AABB = net.minecraft.world.level.block.Block.column(14.0, 0.0, 4.0).toAabbs().getFirst();
}
