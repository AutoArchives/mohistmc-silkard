package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.sniffer;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(Sniffer.class)
public class MixinSniffer {

    // CraftBukkit - EntityDropItemEvent
    @Redirect(method = "lambda$dropSeed$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$dropSeed(ServerLevel level, Entity entity) {
        EntityDropItemEvent event = new EntityDropItemEvent(((Sniffer) (Object) this).getBukkitEntity(), (org.bukkit.entity.Item) entity.getBukkitEntity());
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled() && level.addFreshEntity(entity);
    }
}
