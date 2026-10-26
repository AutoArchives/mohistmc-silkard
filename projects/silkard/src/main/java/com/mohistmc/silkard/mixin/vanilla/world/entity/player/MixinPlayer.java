package com.mohistmc.silkard.mixin.vanilla.world.entity.player;

import com.mohistmc.silkard.injected.world.entity.player.ContextPlayer;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.spongepowered.asm.mixin.Shadow;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.player.PlayerSpawnChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(Player.class)
public abstract class MixinPlayer implements ContextPlayer {

    // CraftBukkit start
    @Unique
    public boolean fauxSleeping;

    @Unique
    public int oldLevel = -1;
    // CraftBukkit end

    @Unique private CraftEntity silkard$playerBukkitEntity;

    @Override
    public boolean silkard$affectsSpawning() {
        return true;
    }

    @Override
    public void silkard$setAffectsSpawning(boolean affectsSpawning) {
    }

    @Override
    public boolean silkard$fauxSleeping() {
        return fauxSleeping;
    }

    @Override
    public void silkard$fauxSleeping(boolean fauxSleeping) {
        this.fauxSleeping = fauxSleeping;
    }

    @Override
    public int silkard$oldLevel() {
        return oldLevel;
    }

    @Override
    public void silkard$setOldLevel(int oldLevel) {
        this.oldLevel = oldLevel;
    }

    // CraftBukkit - runtime NMS has no concrete getBukkitEntity() on Player (only the ContextPlayer stub); provide the real lazily-created entity.
    public CraftHumanEntity getBukkitEntity() {
        if (this.silkard$playerBukkitEntity == null) {
            this.silkard$playerBukkitEntity = CraftEntity.getEntity(((Entity) (Object) this).level().getCraftServer(), (Entity) (Object) this);
        }
        return (CraftHumanEntity) this.silkard$playerBukkitEntity;
    }
}
