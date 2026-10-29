package com.mohistmc.silkard.mixin.vanilla.world.entity;

import com.mohistmc.silkard.injected.world.entity.ContextMob;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.ConversionTracker;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(ConversionTracker.class)
public class MixinConversionTracker {

    // CraftBukkit start
    @Redirect(method = "doConversion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;convertTo(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/ConversionParams;Lnet/minecraft/world/entity/ConversionParams$AfterConversion;)Lnet/minecraft/world/entity/Mob;"))
    private <T extends Mob> T silkard$convertTo(Mob mob, EntityType<T> entityType, ConversionParams params, ConversionParams.AfterConversion<T> afterConversion) {
        if (mob instanceof Skeleton && entityType == EntityTypes.STRAY) {
            T converted = ((ContextMob) mob).convertTo(entityType, params, afterConversion, EntityTransformEvent.TransformReason.FROZEN, CreatureSpawnEvent.SpawnReason.FROZEN);
            if (converted == null) {
                // SPIGOT-7997: End conversion to stop event spam
                ((org.bukkit.entity.Skeleton) mob.getBukkitEntity()).setConversionTime(-1);
            }
            return converted;
        }
        return mob.convertTo(entityType, params, afterConversion);
    }
    // CraftBukkit end
}
