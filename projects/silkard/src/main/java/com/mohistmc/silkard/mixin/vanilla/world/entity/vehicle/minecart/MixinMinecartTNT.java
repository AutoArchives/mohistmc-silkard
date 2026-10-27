package com.mohistmc.silkard.mixin.vanilla.world.entity.vehicle.minecart;

import com.mohistmc.silkard.injected.world.entity.vehicle.minecart.ContextMinecartTNT;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/5/1 18:22
 */
@Mixin(MinecartTNT.class)
public class MixinMinecartTNT implements ContextMinecartTNT {

    public boolean isIncendiary = false; // CraftBukkit - add field

    public boolean silkard$isIncendiary() {
        return isIncendiary;
    }

    public void silkard$isIncendiary(boolean isIncendiary) {
        this.isIncendiary = isIncendiary;
    }

    @Shadow
    public int fuse;

    @Shadow
    public float explosionPowerBase;

    @Shadow
    public float explosionSpeedFactor;

    @Shadow
    public boolean isPrimed() {
        throw new AssertionError();
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - ExplosionPrimeEvent and EntityRemoveEvent cause
     */
    @Overwrite
    public void explode(DamageSource damageSource, double speedSqr) {
        MinecartTNT self = (MinecartTNT) (Object) this;
        if (self.level() instanceof ServerLevel level) {
            if (level.getGameRules().get(GameRules.TNT_EXPLODES)) {
                double speed = Math.min(Math.sqrt(speedSqr), 5.0);
                // CraftBukkit start
                ExplosionPrimeEvent event = new ExplosionPrimeEvent(self.getBukkitEntity(), (float) (this.explosionPowerBase + this.explosionSpeedFactor * self.getRandom().nextDouble() * 1.5 * speed), this.isIncendiary);
                level.getCraftServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    this.fuse = -1;
                    return;
                }
                level.explode(self, damageSource, (ExplosionDamageCalculator) null, self.getX(), self.getY(), self.getZ(), event.getRadius(), event.getFire(), Level.ExplosionInteraction.TNT);
                // CraftBukkit end
                self.discard(EntityRemoveEvent.Cause.EXPLODE); // CraftBukkit
            } else if (this.isPrimed()) {
                self.discard(EntityRemoveEvent.Cause.EXPLODE); // CraftBukkit
            }
        }
    }
}
