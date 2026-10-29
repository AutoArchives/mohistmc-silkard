package com.mohistmc.silkard.mixin.vanilla.world.entity.boss.enderdragon.phases;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(EnderDragonPhaseManager.class)
public abstract class MixinEnderDragonPhaseManager {

    // @formatter:off
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final private EnderDragon dragon;
    @Shadow private DragonPhaseInstance currentPhase;
    @Shadow public abstract <T extends DragonPhaseInstance> T getPhase(EnderDragonPhase<T> phase);
    // @formatter:on

    // CraftBukkit start - Call EnderDragonChangePhaseEvent
    /**
     * @author Mgazul
     * @reason CraftBukkit - EnderDragonChangePhaseEvent
     */
    @Overwrite
    public void setPhase(EnderDragonPhase<?> target) {
        if (this.currentPhase != null && target == this.currentPhase.getPhase()) {
            return;
        }
        if (this.currentPhase != null) {
            this.currentPhase.end();
        }
        EnderDragonChangePhaseEvent event = new EnderDragonChangePhaseEvent(
                (CraftEnderDragon) this.dragon.getBukkitEntity(),
                (this.currentPhase == null) ? null : CraftEnderDragon.getBukkitPhase(this.currentPhase.getPhase()),
                CraftEnderDragon.getBukkitPhase(target)
        );
        this.dragon.level().getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        target = CraftEnderDragon.getMinecraftPhase(event.getNewPhase());
        this.currentPhase = this.getPhase(target);
        if (!this.dragon.level().isClientSide()) {
            this.dragon.getEntityData().set(EnderDragon.DATA_PHASE, target.getId());
        }
        LOGGER.debug("Dragon is now in phase {} on the {}", target, this.dragon.level().isClientSide() ? "client" : "server");
        this.currentPhase.begin();
    }
    // CraftBukkit end
}
