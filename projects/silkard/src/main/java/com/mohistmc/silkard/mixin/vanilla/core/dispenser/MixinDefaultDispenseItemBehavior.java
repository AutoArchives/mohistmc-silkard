package com.mohistmc.silkard.mixin.vanilla.core.dispenser;

import com.mohistmc.silkard.bukkit.DispenseEventHooks;
import com.mohistmc.silkard.injected.core.dispenser.ContextDefaultDispenseItemBehavior;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DefaultDispenseItemBehavior.class)
public abstract class MixinDefaultDispenseItemBehavior implements ContextDefaultDispenseItemBehavior {

    // CraftBukkit start
    @Unique
    private boolean silkard$dropper;

    @Override
    public void silkard$dropper(boolean dropper) {
        this.silkard$dropper = dropper;
    }

    @Override
    public boolean silkard$dropper() {
        return this.silkard$dropper;
    }
    // CraftBukkit end

    /**
     * @author Mgazul
     * @reason CraftBukkit - fire BlockDispenseEvent in spawnItem
     */
    @Overwrite
    protected ItemStack execute(BlockSource source, ItemStack dispensed) {
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        Position position = DispenserBlock.getDispensePosition(source);
        ItemStack itemStack = dispensed.split(1);

        // CraftBukkit start
        if (!DispenseEventHooks.spawnItem(source.level(), itemStack, 6, direction, source, this.silkard$dropper)) {
            dispensed.grow(1);
        }
        // CraftBukkit end
        return dispensed;
    }
}
