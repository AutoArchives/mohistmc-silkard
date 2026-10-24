package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.AssignProfessionFromJobSite;
import net.minecraft.world.entity.npc.villager.Villager;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(AssignProfessionFromJobSite.class)
public class MixinAssignProfessionFromJobSite {

    /**
     * @author Mgazul
     * @reason CraftBukkit - fire VillagerCareerChangeEvent where Villager gets employed
     */
    @Overwrite
    private static void lambda$create$6(Villager body, ServerLevel level, Holder.Reference profession) {
        // CraftBukkit start
        VillagerCareerChangeEvent event = CraftEventFactory.callVillagerCareerChangeEvent(body, CraftVillager.CraftProfession.minecraftHolderToBukkit((Holder<net.minecraft.world.entity.npc.villager.VillagerProfession>) profession), VillagerCareerChangeEvent.ChangeReason.EMPLOYED);
        if (event.isCancelled()) {
            return;
        }
        body.setVillagerData(body.getVillagerData().withProfession(CraftVillager.CraftProfession.bukkitToMinecraftHolder(event.getProfession())));
        // CraftBukkit end
        body.refreshBrain(level);
    }
}
