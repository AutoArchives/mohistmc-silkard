package com.mohistmc.silkard.mixin.vanilla.server.bossevents;

import com.mohistmc.silkard.injected.server.bossevents.ContextCustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvent;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.craftbukkit.boss.CraftKeyedBossbar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(CustomBossEvent.class)
public class MixinCustomBossEvent implements ContextCustomBossEvent {

    @Unique
    private KeyedBossBar bossBar;

    @Override
    public KeyedBossBar getBukkitEntity() {
        if (this.bossBar == null) {
            this.bossBar = new CraftKeyedBossbar((CustomBossEvent) (Object) this);
        }
        return this.bossBar;
    }
}
