package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.goal;

import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(RunAroundLikeCrazyGoal.class)
public class MixinRunAroundLikeCrazyGoal {

    @Shadow
    private AbstractHorse horse;

    @Shadow
    protected int adjustedTickDelay(int delay) {
        throw new AssertionError();
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - EntityTameEvent
     */
    @Overwrite
    public void tick() {
        if (!this.horse.isTamed() && this.horse.getRandom().nextInt(this.adjustedTickDelay(50)) == 0) {
            net.minecraft.world.entity.Entity passenger = this.horse.getFirstPassenger();
            if (passenger == null) {
                return;
            }

            if (passenger instanceof Player player) {
                int temper = this.horse.getTemper();
                int maxTemper = this.horse.getMaxTemper();
                // CraftBukkit start - fire EntityTameEvent
                if (maxTemper > 0 && this.horse.getRandom().nextInt(maxTemper) < temper && !CraftEventFactory.callEntityTameEvent(this.horse, ((CraftHumanEntity) this.horse.getBukkitEntity().getPassenger()).getHandle()).isCancelled()) {
                    this.horse.tameWithName(player);
                    return;
                }
                // CraftBukkit end

                this.horse.modifyTemper(5);
            }

            this.horse.ejectPassengers();
            this.horse.makeMad();
            this.horse.level().broadcastEntityEvent(this.horse, (byte) 6);
        }
    }
}
