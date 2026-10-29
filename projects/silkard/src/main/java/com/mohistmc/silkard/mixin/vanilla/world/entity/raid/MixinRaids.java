package com.mohistmc.silkard.mixin.vanilla.world.entity.raid;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(Raids.class)
public abstract class MixinRaids {

    @Shadow
    @Final
    private Int2ObjectMap<Raid> raidMap;

    @Invoker("getUniqueId")
    protected abstract int silkard$getUniqueId();

    // fixed a bug with raid: players could add up Bad Omen level even when the raid had finished
    @Redirect(method = "createOrExtendRaid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;getMaxRaidOmenLevel()I"))
    private static int silkard$maxOmenLevel(Raid raid) {
        return raid.isInProgress() ? raid.getMaxRaidOmenLevel() : -1;
    }

    @Inject(method = "createOrExtendRaid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;absorbRaidOmen(Lnet/minecraft/server/level/ServerPlayer;)Z"), cancellable = true)
    private void silkard$trigger(ServerPlayer player, BlockPos pos, CallbackInfoReturnable<Raid> cir, @Local(ordinal = 0) Raid raid) {
        if (!CraftEventFactory.callRaidTriggerEvent(raid, player.level(), player)) {
            player.removeEffect(MobEffects.RAID_OMEN);
            if (!raid.isStarted()) {
                this.raidMap.remove(this.silkard$getUniqueId());
            }
            cir.setReturnValue(null);
        }
    }
}
