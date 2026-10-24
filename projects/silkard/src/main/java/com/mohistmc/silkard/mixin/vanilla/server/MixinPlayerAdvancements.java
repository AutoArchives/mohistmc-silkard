package com.mohistmc.silkard.mixin.vanilla.server;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.event.player.PlayerAdvancementCriterionGrantEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(PlayerAdvancements.class)
public abstract class MixinPlayerAdvancements {

    @Shadow
    private ServerPlayer player;

    @Shadow
    public abstract AdvancementProgress getOrStartProgress(AdvancementHolder advancement);

    @Inject(method = "award", at = @At("HEAD"), cancellable = true)
    private void silkard$fireAdvancementCriterionGrantEvent(AdvancementHolder holder, String criterion, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - fire PlayerAdvancementCriterionGrantEvent
        AdvancementProgress advancementprogress = this.getOrStartProgress(holder);
        CriterionProgress criterionprogress = advancementprogress.getCriterion(criterion);

        if (criterionprogress != null && !criterionprogress.isDone()) {
            PlayerAdvancementCriterionGrantEvent event = new PlayerAdvancementCriterionGrantEvent(this.player.getBukkitEntity(), holder.toBukkit(), criterion);
            this.player.level().getCraftServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                cir.setReturnValue(false);
            }
        }
        // CraftBukkit end
    }
}
