package com.mohistmc.silkard.mixin.vanilla.server.level;

import com.mohistmc.silkard.injected.server.level.ContextServerEntity;
import net.minecraft.server.level.ServerEntity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ServerEntity.class)
public class MixinServerEntity implements ContextServerEntity {

    // CraftBukkit start - per-entity tracked player set (26.3: Synchronizer 已内含过滤, 此处保留桥接槽位)
    @org.spongepowered.asm.mixin.Unique
    private java.util.Set<net.minecraft.server.network.ServerPlayerConnection> silkard$trackedPlayers = java.util.concurrent.ConcurrentHashMap.newKeySet();

    @Override
    public void setTrackedPlayers(java.util.Set<net.minecraft.server.network.ServerPlayerConnection> trackedPlayers) {
        this.silkard$trackedPlayers = trackedPlayers;
    }

    public java.util.Set<net.minecraft.server.network.ServerPlayerConnection> silkard$trackedPlayers() {
        return this.silkard$trackedPlayers;
    }
    // CraftBukkit end
}
