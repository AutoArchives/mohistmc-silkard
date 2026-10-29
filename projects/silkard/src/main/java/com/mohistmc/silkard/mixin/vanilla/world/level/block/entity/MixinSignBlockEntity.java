package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import java.util.List;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SignTextSlot;
import org.bukkit.block.sign.Side;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.event.block.SignChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(SignBlockEntity.class)
public class MixinSignBlockEntity {

    @Unique
    private Player silkard$editor;

    @Shadow
    private static SignText updateMessages(List<FilteredText> lines, SignText text, boolean front) {
        throw new AssertionError();
    }

    // CraftBukkit start - SignChangeEvent
    @Inject(method = "updateSignText", at = @At("HEAD"))
    private void silkard$captureEditor(Player player, SignTextSlot slot, List<FilteredText> lines, CallbackInfo ci) {
        this.silkard$editor = player;
    }

    @Redirect(method = "updateSignText", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SignBlockEntity;updateMessages(Ljava/util/List;Lnet/minecraft/world/level/block/entity/SignText;Z)Lnet/minecraft/world/level/block/entity/SignText;"))
    private SignText silkard$signChange(List<FilteredText> lines, SignText text, boolean front) {
        SignText originalText = text;
        SignText updated = updateMessages(lines, text, front);
        Player player = this.silkard$editor;

        if (player == null || this.getLevel() == null) {
            return updated;
        }

        String[] linesArray = new String[4];
        List<Component> messages = updated.getMessages(player.isTextFilteringEnabled());
        for (int i = 0; i < linesArray.length && i < messages.size(); ++i) {
            linesArray[i] = CraftChatMessage.fromComponent(messages.get(i));
        }

        SignChangeEvent event = new SignChangeEvent(CraftBlock.at(this.getLevel(), this.getBlockPos()), player.getBukkitEntity(), linesArray.clone(), front ? Side.FRONT : Side.BACK);
        this.getLevel().getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return originalText;
        }

        Component[] components = CraftSign.sanitizeLines(event.getLines());
        SignText.Mutable mutable = updated.asMutable();
        for (int i = 0; i < components.length; i++) {
            if (!Objects.equals(linesArray[i], event.getLine(i))) {
                mutable.setLine(i, components[i]);
            }
        }
        return mutable.asImmutable();
    }
    // CraftBukkit end
}
