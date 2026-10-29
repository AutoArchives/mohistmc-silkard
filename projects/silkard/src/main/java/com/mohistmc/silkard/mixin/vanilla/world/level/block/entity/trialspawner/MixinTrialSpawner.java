package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity.trialspawner;

import com.mohistmc.silkard.injected.server.level.ContextServerLevel;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(TrialSpawner.class)
public class MixinTrialSpawner {

    @Unique
    private BlockPos silkard$spawnerPos;

    @Unique
    private BlockPos silkard$ejectPos;

    @Unique
    private ServerLevel silkard$ejectLevel;

    // CraftBukkit start - TrialSpawnerSpawnEvent
    @Inject(method = "spawnMob", at = @At("HEAD"))
    private void silkard$captureSpawnerPos(ServerLevel level, BlockPos spawnerPos, CallbackInfoReturnable<Optional<UUID>> cir) {
        this.silkard$spawnerPos = spawnerPos;
    }

    @Redirect(method = "spawnMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tryAddFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$spawnMob(ServerLevel level, Entity entity) {
        if (CraftEventFactory.callTrialSpawnerSpawnEvent(entity, this.silkard$spawnerPos).isCancelled()) {
            return false;
        }
        return ((ContextServerLevel) level).tryAddFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER);
    }
    // CraftBukkit end

    // CraftBukkit start - BlockDispenseLootEvent
    @Inject(method = "ejectReward", at = @At("HEAD"))
    private void silkard$captureEjectPos(ServerLevel level, BlockPos pos, net.minecraft.resources.ResourceKey<LootTable> ejectingLootTable, CallbackInfo ci) {
        this.silkard$ejectPos = pos;
        this.silkard$ejectLevel = level;
    }

    @Redirect(method = "ejectReward", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
    private ObjectArrayList<ItemStack> silkard$loot(LootTable table, LootParams params) {
        ObjectArrayList<ItemStack> loot = table.getRandomItems(params);
        BlockDispenseLootEvent event = CraftEventFactory.callBlockDispenseLootEvent(this.silkard$ejectLevel, this.silkard$ejectPos, null, loot);
        if (event.isCancelled()) {
            return new ObjectArrayList<>();
        }
        return new ObjectArrayList<>(event.getDispensedLoot().stream().map(CraftItemStack::asNMSCopy).toList());
    }
    // CraftBukkit end
}
