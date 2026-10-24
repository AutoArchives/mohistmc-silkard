package com.mohistmc.silkard.mixin.vanilla.core.dispenser;

import com.mohistmc.silkard.bukkit.DispenseEventHooks;
import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.block.DispenserBlock;
import org.bukkit.craftbukkit.projectiles.CraftBlockProjectileSource;
import org.bukkit.event.block.BlockDispenseEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ProjectileDispenseBehavior.class)
public abstract class MixinProjectileDispenseBehavior {

    @Shadow
    @Final
    private ProjectileItem projectileItem;

    @Shadow
    @Final
    private ProjectileItem.DispenseConfig dispenseConfig;

    /**
     * @author Mgazul
     * @reason CraftBukkit - BlockDispenseEvent
     */
    @Overwrite
    public ItemStack execute(BlockSource source, ItemStack dispensed) {
        ServerLevel serverlevel = source.level();
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        Position position = this.dispenseConfig.positionFunction().getDispensePosition(source, direction);

        // CraftBukkit start
        ItemStack itemstack1 = dispensed.split(1);
        BlockDispenseEvent event = DispenseEventHooks.callBlockDispenseEvent(serverlevel, source, itemstack1, new org.bukkit.util.Vector(direction.getStepX(), direction.getStepY(), direction.getStepZ()));
        if (event.isCancelled()) {
            dispensed.grow(1);
            return dispensed;
        }

        if (DispenseEventHooks.chainToChangedItem(source, itemstack1, event, this)) {
            dispensed.grow(1);
            return dispensed;
        }

        // SPIGOT-7923: Avoid create projectiles with empty item
        if (!itemstack1.isEmpty()) {
            Projectile projectile = Projectile.spawnProjectileUsingShoot(this.projectileItem.asProjectile(serverlevel, position, itemstack1, direction), serverlevel, itemstack1, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), this.dispenseConfig.power(), this.dispenseConfig.uncertainty());
            ((ContextEntity) projectile).silkard_projectileSource(new CraftBlockProjectileSource(source.blockEntity()));
        }
        // dispensed.shrink(1); // CraftBukkit - Handled during event processing
        // CraftBukkit end
        return dispensed;
    }
}
