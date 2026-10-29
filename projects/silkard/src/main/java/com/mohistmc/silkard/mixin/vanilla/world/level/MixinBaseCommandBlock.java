package com.mohistmc.silkard.mixin.vanilla.world.level;

import com.mohistmc.silkard.injected.commands.ContextCommands;
import com.mohistmc.silkard.injected.world.level.ContextBaseCommandBlock;
import com.mohistmc.silkard.injected.world.level.ContextCloseableCommandBlockSource;
import java.lang.reflect.Constructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.CraftBlockCommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseCommandBlock.class)
public abstract class MixinBaseCommandBlock implements ContextBaseCommandBlock {

    @Shadow
    @Final
    private boolean trackOutput;

    @Unique
    private static final Constructor<?> SILKARD_SOURCE_CTOR;

    static {
        try {
            Constructor<?> ctor = Class.forName("net.minecraft.world.level.BaseCommandBlock$CloseableCommandBlockSource")
                    .getDeclaredConstructor(BaseCommandBlock.class, ServerLevel.class);
            ctor.setAccessible(true);
            SILKARD_SOURCE_CTOR = ctor;
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public CommandSender getBukkitSender(CommandSourceStack wrapper) {
        if (wrapper.getEntity() != null) {
            return wrapper.getEntity().getBukkitEntity();
        }

        ServerLevel level = wrapper.getLevel();
        BlockEntity blockEntity = level != null ? level.getBlockEntity(BlockPos.containing(wrapper.getPosition())) : null;
        return blockEntity != null ? new CraftBlockCommandSender(wrapper, blockEntity) : null;
    }

    // CraftBukkit - keep a source even when output is disabled, merely closed
    @Inject(method = "createSource", at = @At("RETURN"), cancellable = true)
    private void silkard$createSource(ServerLevel level, CallbackInfoReturnable<Object> cir) {
        if (cir.getReturnValue() != null) {
            return;
        }

        try {
            Object source = SILKARD_SOURCE_CTOR.newInstance((Object) this, level);
            ((ContextCloseableCommandBlockSource) source).silkard$setClosed(true);
            cir.setReturnValue(source);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Redirect(method = "performCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V"))
    private void silkard$dispatchServerCommand(Commands commands, CommandSourceStack sender, String command) {
        // CraftBukkit - ServerCommandEvent, blocked commands (stop/kick/op/...) and command-block-overrides
        ((ContextCommands) commands).dispatchServerCommand(sender, command);
    }
}
