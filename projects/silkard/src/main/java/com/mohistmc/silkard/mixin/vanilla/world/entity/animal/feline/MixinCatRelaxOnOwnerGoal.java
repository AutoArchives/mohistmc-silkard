package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.feline;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.feline.Cat;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(targets = "net.minecraft.world.entity.animal.feline.Cat$CatRelaxOnOwnerGoal")
public class MixinCatRelaxOnOwnerGoal {

    @Shadow @Final private Cat cat;

    // CraftBukkit start - fire EntityDropItemEvent for the morning gift
    @Redirect(method = "lambda$giveMorningGift$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$dropGift(ServerLevel level, Entity entity) {
        EntityDropItemEvent event = new EntityDropItemEvent(cat.getBukkitEntity(), (org.bukkit.entity.Item) entity.getBukkitEntity());
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled() && level.addFreshEntity(entity);
    }
    // CraftBukkit end
}
