package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.BeehiveBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeehiveBlock.class)
public class MixinBeehiveBlock {

    // 26.3 把 tick 里的激怒逻辑抽到 angerNearbyBees，且 INVOKE owner 是 Bee 不是 Mob
    @Redirect(method = "angerNearbyBees", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/bee/Bee;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void silkard_setTarget(net.minecraft.world.entity.animal.bee.Bee bee, LivingEntity target) {
        bee.setTarget(target, org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);
    }
}
