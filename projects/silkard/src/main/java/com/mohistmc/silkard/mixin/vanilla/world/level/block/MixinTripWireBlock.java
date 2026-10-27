package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.ContextLevel;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(TripWireBlock.class)
public abstract class MixinTripWireBlock {

    // CraftBukkit start - Call interact even when triggering connected tripwire
    @Inject(method = "checkPressed(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"), cancellable = true)
    private void silkard$interactEvent(Level level, BlockPos pos, List<? extends Entity> entities, CallbackInfo ci) {
        boolean triggered = false;
        for (Entity entity : entities) {
            if (!entity.isIgnoringBlockTriggers()) {
                triggered = true;
                break;
            }
        }
        if (!triggered) {
            return;
        }
        BlockState state = level.getBlockState(pos);
        if (!state.getValue(TripWireBlock.ATTACHED)) {
            return;
        }
        org.bukkit.World bworld = ((ContextLevel) level).getWorld();
        org.bukkit.plugin.PluginManager manager = level.getCraftServer().getPluginManager();
        org.bukkit.block.Block block = bworld.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        boolean allowed = false;

        // If all of the events are cancelled block the tripwire trigger, else allow
        for (Entity entity : entities) {
            org.bukkit.event.Cancellable cancellable;

            if (entity instanceof Player player) {
                cancellable = CraftEventFactory.callPlayerInteractEvent(player, Action.PHYSICAL, pos, null, null, null);
            } else {
                cancellable = new EntityInteractEvent(entity.getBukkitEntity(), block);
                manager.callEvent((EntityInteractEvent) cancellable);
            }

            if (!cancellable.isCancelled()) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
