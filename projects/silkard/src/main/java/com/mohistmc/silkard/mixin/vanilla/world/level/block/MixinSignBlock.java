package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.block.ContextSignBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignTextSlot;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.player.PlayerSignOpenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(SignBlock.class)
public class MixinSignBlock implements ContextSignBlock {

    @Redirect(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SignBlock;openTextEdit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/entity/SignBlockEntity;Lnet/minecraft/world/level/block/entity/SignTextSlot;)V"))
    private void silkard$openTextEditInteract(SignBlock instance, Player player, SignBlockEntity sign, SignTextSlot slot) {
        instance.openTextEdit(player, sign, slot, PlayerSignOpenEvent.Cause.INTERACT); // CraftBukkit
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - fire PlayerSignOpenEvent
     */
    @Overwrite
    public void openTextEdit(Player player, SignBlockEntity sign, SignTextSlot slot) {
        this.openTextEdit(player, sign, slot, PlayerSignOpenEvent.Cause.UNKNOWN);
    }

    @Override
    public void openTextEdit(Player player, SignBlockEntity sign, SignTextSlot slot, PlayerSignOpenEvent.Cause cause) {
        if (!CraftEventFactory.callPlayerSignOpenEvent(player, sign, slot, cause)) {
            return;
        }
        sign.setAllowedPlayerEditor(player.getUUID());
        player.openTextEdit(sign, slot);
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - remove unnecessary sign ticking
     */
    @Overwrite
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return null;
    }
}
