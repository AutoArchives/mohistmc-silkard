package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.frog;

import com.mohistmc.silkard.injected.world.entity.ContextMob;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(Tadpole.class)
public abstract class MixinTadpole {

    @Invoker("setAge")
    protected abstract void silkard$setAge(int age);

    // CraftBukkit start - add spawn and transform reasons
    @Redirect(method = "ageUp()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/frog/Tadpole;convertTo(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/ConversionParams;Lnet/minecraft/world/entity/ConversionParams$AfterConversion;)Lnet/minecraft/world/entity/Mob;"))
    private Mob silkard$growUp(Tadpole instance, EntityType<Frog> entityType, ConversionParams params, ConversionParams.AfterConversion<Frog> afterConversion) {
        Mob converted = ((ContextMob) instance).convertTo(entityType, params, afterConversion, EntityTransformEvent.TransformReason.METAMORPHOSIS, CreatureSpawnEvent.SpawnReason.METAMORPHOSIS);
        if (converted == null) {
            this.silkard$setAge(0); // avoid a loop when the event is cancelled
        }
        return converted;
    }
    // CraftBukkit end
}
