package com.mohistmc.silkard.mixin.vanilla.world.level.storage.loot;

import com.mohistmc.silkard.injected.world.level.storage.loot.ContextLootTable;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.world.LootGenerateEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(LootTable.class)
public class MixinLootTable implements ContextLootTable {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    private Optional<Identifier> randomSequence;

    @Shadow
    private List<Integer> getAvailableSlots(Container container, RandomSource random) {
        throw new AssertionError();
    }

    @Shadow
    private void shuffleAndSplitItems(ObjectArrayList<ItemStack> result, int availableSlots, RandomSource random) {
        throw new AssertionError();
    }

    @Shadow
    private ObjectArrayList<ItemStack> getRandomItems(LootContext context) {
        throw new AssertionError();
    }

    // CraftBukkit start
    @Unique
    public CraftLootTable craftLootTable;
    // CraftBukkit end

    // CraftBukkit start - LootGenerateEvent
    @Overwrite
    public void fill(Container container, LootParams params, long optionalRandomSeed) {
        this.fillInventory(container, params, optionalRandomSeed, false);
    }

    @Override
    public void fillInventory(Container container, LootParams params, long optionalRandomSeed, boolean plugin) {
        LootContext context = new LootContext.Builder(params).withOptionalRandomSeed(optionalRandomSeed).create(this.randomSequence);
        ObjectArrayList<ItemStack> itemStacks = this.getRandomItems(context);
        RandomSource random = context.getRandom();

        LootGenerateEvent event = CraftEventFactory.callLootGenerateEvent(container, (LootTable) (Object) this, context, itemStacks, plugin);
        if (event.isCancelled()) {
            return;
        }
        itemStacks = event.getLoot().stream().map(CraftItemStack::asNMSCopy).collect(ObjectArrayList.toList());

        List<Integer> availableSlots = this.getAvailableSlots(container, random);
        this.shuffleAndSplitItems(itemStacks, availableSlots.size(), random);

        for (ItemStack itemStack : itemStacks) {
            if (availableSlots.isEmpty()) {
                LOGGER.warn("Tried to over-fill a container");
                return;
            }

            if (itemStack.isEmpty()) {
                container.setItem(availableSlots.remove(availableSlots.size() - 1), ItemStack.EMPTY);
            } else {
                container.setItem(availableSlots.remove(availableSlots.size() - 1), itemStack);
            }
        }
    }
    // CraftBukkit end

    @Override
    public CraftLootTable silkard$craftLootTable() {
        return this.craftLootTable;
    }

    @Override
    public void silkard$craftLootTable(CraftLootTable craftLootTable) {
        this.craftLootTable = craftLootTable;
    }
}
