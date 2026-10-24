package com.mohistmc.silkard.mixin.vanilla.world.entity.npc.villager;

import com.mohistmc.silkard.injected.world.entity.npc.villager.ContextVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(Villager.class)
public class MixinVillager implements ContextVillager {

    // CB 自增字段，26.3 vanilla 无此成员；初值取 vanilla 原硬编码 24000L
    @Unique
    public long gossipDecayInterval = 24000L;

    @Override
    public long silkard$gossipDecayInterval() {
        return gossipDecayInterval;
    }

    @Override
    public void silkard$gossipDecayInterval(long gossipDecayInterval) {
        this.gossipDecayInterval = gossipDecayInterval;
    }
}
