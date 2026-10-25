package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(CommandBlockEntity.class)
public class MixinCommandBlockEntity {

    // CraftBukkit - getBukkitSender 覆盖点在匿名内部类 CommandBlockEntity$1（BaseCommandBlock 实现）内；
    // 26.3 CommandBlockEntity 与 BlockEntity 均无 tick 方法，原 tick 锚点无效，require=0 保持可启动
    @Inject(method = "getCommandBlock", at = @At("HEAD"), require = 0)
    private void silkard$getCommandBlock(CallbackInfoReturnable<BaseCommandBlock> cir) {
    }
}
