package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.hoglin;

import com.mohistmc.silkard.injected.world.entity.ContextMob;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(Hoglin.class)
public class MixinHoglin {

    // CraftBukkit start - add spawn and transform reasons
    @Redirect(method = "finishConversion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/hoglin/Hoglin;convertTo(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/ConversionParams;Lnet/minecraft/world/entity/ConversionParams$AfterConversion;)Lnet/minecraft/world/entity/Mob;"))
    private Mob silkard$finishConversion(Hoglin instance, EntityType<Zoglin> entityType, ConversionParams params, ConversionParams.AfterConversion<Zoglin> afterConversion) {
        return ((ContextMob) instance).convertTo(entityType, params, afterConversion, EntityTransformEvent.TransformReason.PIGLIN_ZOMBIFIED, CreatureSpawnEvent.SpawnReason.PIGLIN_ZOMBIFIED);
    }
    // CraftBukkit end
}
