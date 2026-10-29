package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.turtle;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(targets = "net.minecraft.world.entity.animal.turtle.Turtle$TurtleLayEggGoal")
public class MixinTurtle$TurtleLayEggGoal {

    @Shadow
    @Final
    private Turtle turtle;

    @Unique
    private boolean silkard$eggAllowed;

    // Sound is emitted from the placement redirect so a cancelled event produces no sound.
    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private boolean silkard$muteSound(Level level, Entity entity, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch) {
        return false;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$eggPlace(Level level, BlockPos pos, BlockState state) {
        this.silkard$eggAllowed = CraftEventFactory.callEntityChangeBlockEvent(this.turtle, pos, state);
        if (!this.silkard$eggAllowed) {
            return false;
        }

        level.playSound(null, ((Entity) (Object) this.turtle).blockPosition(), SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, level.getRandom().nextFloat() * 0.2F + 0.9F);
        return level.setBlockAndUpdate(pos, state);
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
    private boolean silkard$eggGameEvent(Level level, Holder<GameEvent> event, BlockPos pos, GameEvent.Context context) {
        return this.silkard$eggAllowed;
    }
}
