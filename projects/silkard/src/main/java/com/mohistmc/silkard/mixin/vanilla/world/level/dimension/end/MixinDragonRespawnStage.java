package com.mohistmc.silkard.mixin.vanilla.world.level.dimension.end;

import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(targets = "net.minecraft.world.level.dimension.end.DragonRespawnStage$4")
public class MixinDragonRespawnStage {

    // CraftBukkit - add Bukkit remove cause
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;discard()V"))
    private void silkard$discard(EndCrystal crystal) {
        crystal.discard(EntityRemoveEvent.Cause.EXPLODE);
    }
}
