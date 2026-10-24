package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.ResetProfession;
import net.minecraft.world.entity.npc.villager.Villager;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(ResetProfession.class)
public class MixinResetProfession {

    /**
     * @author Mgazul
     * @reason CraftBukkit - fire VillagerCareerChangeEvent when losing job
     */
    @Overwrite
    private static boolean lambda$create$2(ServerLevel level, Villager body, long timestamp) {
        net.minecraft.world.entity.npc.villager.VillagerData bodyData = body.getVillagerData();
        boolean canBeFired = !bodyData.profession().is(net.minecraft.world.entity.npc.villager.VillagerProfession.NONE) && !bodyData.profession().is(net.minecraft.world.entity.npc.villager.VillagerProfession.NITWIT);
        if (canBeFired && body.getVillagerXp() == 0 && bodyData.level() <= 1) {
            // CraftBukkit start
            VillagerCareerChangeEvent event = CraftEventFactory.callVillagerCareerChangeEvent(body, CraftVillager.CraftProfession.minecraftHolderToBukkit(level.registryAccess().getOrThrow(net.minecraft.world.entity.npc.villager.VillagerProfession.NONE)), VillagerCareerChangeEvent.ChangeReason.LOSING_JOB);
            if (event.isCancelled()) {
                return false;
            }
            body.setVillagerData(body.getVillagerData().withProfession(CraftVillager.CraftProfession.bukkitToMinecraftHolder(event.getProfession())));
            // CraftBukkit end
            body.refreshBrain(level);
            return true;
        } else {
            return false;
        }
    }
}
