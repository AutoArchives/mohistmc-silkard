package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.creaking;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.creaking.Creaking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Creaking.class)
public class MixinCreaking {

    // 26.3 的 remove(RemovalReason) 调用上移到 tearDown（die 只在 LivingEntity）
    @Redirect(method = "tearDown", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/creaking/Creaking;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void silkard_remove(Creaking creaking, Entity.RemovalReason reason) {
        creaking.remove(Entity.RemovalReason.DISCARDED, null);
    }
}
