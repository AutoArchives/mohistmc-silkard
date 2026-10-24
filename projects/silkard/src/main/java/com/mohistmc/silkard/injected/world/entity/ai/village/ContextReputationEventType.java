package com.mohistmc.silkard.injected.world.entity.ai.village;

import net.minecraft.world.entity.ai.village.ReputationEventType;

/**
 * @author Mgazul
 * @date 2026/5/13 21:12
 */
public interface ContextReputationEventType {

    // CraftBukkit start - additional events added in the API
    ReputationEventType GOSSIP = ReputationEventType.register("bukkit_gossip");
    ReputationEventType DECAY = ReputationEventType.register("bukkit_decay");
    ReputationEventType UNSPECIFIED = ReputationEventType.register("bukkit_unspecified");
    // CraftBukkit end

    // Declared last so the bukkit constants above are already initialised when the map is built
    java.util.Map<String, ReputationEventType> BY_ID = silkard$collect();

    static java.util.Map<String, ReputationEventType> silkard$collect() {
        java.util.Map<String, ReputationEventType> map = com.google.common.collect.Maps.newHashMap();
        map.put("zombie_villager_cured", ReputationEventType.ZOMBIE_VILLAGER_CURED);
        map.put("golem_killed", ReputationEventType.GOLEM_KILLED);
        map.put("villager_hurt", ReputationEventType.VILLAGER_HURT);
        map.put("villager_killed", ReputationEventType.VILLAGER_KILLED);
        map.put("trade", ReputationEventType.TRADE);
        map.put("bukkit_gossip", ReputationEventType.GOSSIP);
        map.put("bukkit_decay", ReputationEventType.DECAY);
        map.put("bukkit_unspecified", ReputationEventType.UNSPECIFIED);
        return map;
    }
}
