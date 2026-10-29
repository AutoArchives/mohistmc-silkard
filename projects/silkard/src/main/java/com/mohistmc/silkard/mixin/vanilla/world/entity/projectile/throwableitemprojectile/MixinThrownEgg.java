package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile.throwableitemprojectile;

import com.mohistmc.silkard.injected.world.level.ContextLevelWriter;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.chicken.ChickenVariant;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(ThrownEgg.class)
public abstract class MixinThrownEgg extends ThrowableItemProjectile {

    protected MixinThrownEgg(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    // CraftBukkit start - egg hatching event and configurable hatching type
    @Overwrite
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            boolean hatching = this.random.nextInt(8) == 0;
            int i = 1;
            if (this.random.nextInt(32) == 0) {
                i = 4;
            }

            org.bukkit.entity.EntityType hatchingType = org.bukkit.entity.EntityType.CHICKEN;
            Entity shooter = this.getOwner();
            if (!hatching) {
                i = 0;
            }
            if (shooter instanceof ServerPlayer) {
                PlayerEggThrowEvent event = new PlayerEggThrowEvent((Player) shooter.getBukkitEntity(),
                        (org.bukkit.entity.Egg) this.getBukkitEntity(), hatching, (byte) i, hatchingType);
                this.level().getCraftServer().getPluginManager().callEvent(event);

                i = event.getNumHatches();
                hatching = event.isHatching();
                hatchingType = event.getHatchingType();
                if (!hatching) {
                    i = 0;
                }
            }

            for (int j = 0; j < i; ++j) {
                Entity chicken = this.level().getWorld().makeEntity(
                        new Location(this.level().getWorld(), this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F),
                        hatchingType.getEntityClass());
                if (chicken != null) {
                    if (chicken.getBukkitEntity() instanceof Ageable) {
                        ((Ageable) chicken.getBukkitEntity()).setBaby();
                    }
                    chicken.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                    Optional<Holder<ChickenVariant>> optional = Optional.ofNullable(this.getItem().get(DataComponents.CHICKEN_VARIANT));
                    if (chicken instanceof Chicken actualChicken) {
                        optional.ifPresent(actualChicken::setVariant);
                    }
                    if (!chicken.fudgePositionAfterSizeChange(EntityDimensions.fixed(0.0F, 0.0F))) {
                        break;
                    }
                    ((ContextLevelWriter) this.level()).addFreshEntity(chicken, CreatureSpawnEvent.SpawnReason.EGG);
                }
            }

            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard(EntityRemoveEvent.Cause.HIT);
        }
    }
    // CraftBukkit end
}
