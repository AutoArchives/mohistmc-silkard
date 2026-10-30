package com.mohistmc.silkard.mixin.vanilla.world.entity.decoration;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(LeashFenceKnotEntity.class)
public class MixinLeashFenceKnotEntity {

    // CraftBukkit start - leash events
    @WrapOperation(method = "interact", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/Leashable;setLeashedTo(Lnet/minecraft/world/entity/Entity;Z)V"))
    private void silkard$leashToKnot(Leashable leashable, Entity holder, boolean sendPacket, Operation<Void> original, Player player, InteractionHand hand, Vec3 position, @Local(ordinal = 0) LocalBooleanRef flag) {
        if (leashable instanceof Entity leashed && CraftEventFactory.callPlayerLeashEntityEvent(leashed, holder, player, hand).isCancelled()) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetEntityLinkPacket(leashed, leashable.getLeashHolder()));
            }
            flag.set(true);
            return;
        }
        original.call(leashable, holder, sendPacket);
    }

    @WrapOperation(method = "interact", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/entity/Leashable;setLeashedTo(Lnet/minecraft/world/entity/Entity;Z)V"))
    private void silkard$unleashFromKnot(Leashable leashable, Entity holder, boolean sendPacket, Operation<Void> original, Player player, InteractionHand hand, Vec3 position, @Local(ordinal = 1) LocalBooleanRef flag1) {
        if (leashable instanceof Entity leashed) {
            if (CraftEventFactory.callPlayerUnleashEntityEvent(leashed, player, hand).isCancelled()) {
                return;
            }
            if (CraftEventFactory.callPlayerLeashEntityEvent(leashed, (Entity) (Object) this, player, hand).isCancelled()) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSetEntityLinkPacket(leashed, leashable.getLeashHolder()));
                }
                flag1.set(true);
                return;
            }
        }
        original.call(leashable, holder, sendPacket);
    }

    @Redirect(method = "notifyLeasheeRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/LeashFenceKnotEntity;discard()V"))
    private void silkard$discard(LeashFenceKnotEntity instance) {
        ((ContextEntity) (Object) instance).discard(EntityRemoveEvent.Cause.DROP);
    }
    // CraftBukkit end
}
