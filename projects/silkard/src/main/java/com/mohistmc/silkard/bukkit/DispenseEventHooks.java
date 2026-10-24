package com.mohistmc.silkard.bukkit;

import net.minecraft.core.Position;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.event.block.BlockDispenseEvent;

/**
 * CraftBukkit 发射器事件补丁的公共实现。
 * 对应 nms-patches: net/minecraft/core/dispenser/* 与 DispenserBlock.eventFired 语义。
 * 注: CraftBukkit 的 eventFired 标志自 1.14 起没有任何代码置 true(每次都会触发事件),
 * 因此这里等价地无条件触发事件。
 */
public final class DispenseEventHooks {

    private DispenseEventHooks() {
    }

    /**
     * 触发 BlockDispenseEvent 并返回事件实例, 由调用方处理取消与物品替换。
     */
    public static BlockDispenseEvent callBlockDispenseEvent(Level level, BlockSource source, ItemStack mirrored, org.bukkit.util.Vector velocity) {
        org.bukkit.block.Block block = CraftBlock.at(level, source.pos());
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(mirrored);
        BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), velocity);
        level.getCraftServer().getPluginManager().callEvent(event);
        return event;
    }

    /**
     * CraftBukkit - 事件物品被替换时链式转发到新物品对应的发射行为。
     *
     * @return 已转发到新行为时为 true
     */
    public static boolean chainToChangedItem(BlockSource source, ItemStack mirrored, BlockDispenseEvent event, Object self) {
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(mirrored);
        if (!event.getItem().equals(craftItem)) {
            // Chain to handler for new item
            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            DispenseItemBehavior behavior = DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
            if (behavior != DispenseItemBehavior.NOOP && behavior != self) {
                behavior.dispense(source, eventStack);
                return true;
            }
        }
        return false;
    }

    /**
     * CraftBukkit - DefaultDispenseItemBehavior.spawnItem 的事件版本 (void -> boolean, dropper 标记)。
     * 与 CB 一致: 取消返回 false; 物品替换且非投掷器时链式转发。
     */
    public static boolean spawnItem(Level level, ItemStack itemStack, int accuracy, Direction direction, BlockSource source, boolean dropper) {
        if (itemStack.isEmpty()) {
            return true;
        }
        Position position = DispenserBlock.getDispensePosition(source);
        ItemEntity itemEntity = prepareItem(level, itemStack, accuracy, direction, position);

        BlockDispenseEvent event = callBlockDispenseEvent(level, source, itemStack, CraftVector.toBukkit(itemEntity.getDeltaMovement()));
        if (event.isCancelled()) {
            return false;
        }

        itemEntity.setItem(CraftItemStack.asNMSCopy(event.getItem()));
        itemEntity.setDeltaMovement(CraftVector.toNMS(event.getVelocity()));

        if (!dropper && !event.getItem().getType().equals(CraftItemStack.asCraftMirror(itemStack).getType())) {
            // Chain to handler for new item
            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            DispenseItemBehavior behavior = DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
            if (behavior != DispenseItemBehavior.NOOP && behavior.getClass() != DefaultDispenseItemBehavior.class) {
                behavior.dispense(source, eventStack);
            } else {
                level.addFreshEntity(itemEntity);
            }
            return false;
        }

        level.addFreshEntity(itemEntity);
        return true;
        // CraftBukkit end
    }

    private static ItemEntity prepareItem(Level level, ItemStack itemStack, int accuracy, Direction direction, Position position) {
        double spawnX = position.x();
        double spawnY = position.y();
        double spawnZ = position.z();
        if (direction.getAxis() == Direction.Axis.Y) {
            spawnY -= 0.125;
        } else {
            spawnY -= 0.15625;
        }

        ItemEntity itemEntity = new ItemEntity(level, spawnX, spawnY, spawnZ, itemStack);
        RandomSource random = level.getRandom();
        double pow = random.nextDouble() * 0.1 + 0.2;
        itemEntity.setDeltaMovement(
                random.triangle(direction.getStepX() * pow, 0.0172275 * accuracy),
                random.triangle(0.2, 0.0172275 * accuracy),
                random.triangle(direction.getStepZ() * pow, 0.0172275 * accuracy)
        );
        return itemEntity;
    }
}
