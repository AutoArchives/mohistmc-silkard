package com.mohistmc.silkard.mixin.vanilla.world.inventory;

import com.mohistmc.silkard.injected.world.inventory.ContextLocationContainer;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.inventory.CraftInventoryEnchanting;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.view.CraftEnchantmentView;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentMenu.class)
public abstract class MixinEnchantmentMenu extends AbstractContainerMenu {

    @Shadow @Final private Container enchantSlots;
    @Shadow @Final private ContainerLevelAccess access;
    @Shadow @Final public int[] costs;
    @Shadow @Final public int[] enchantClue;
    @Shadow @Final public int[] levelClue;

    // CraftBukkit start
    @Unique
    private CraftEnchantmentView bukkitEntity = null;

    @Unique
    private org.bukkit.entity.Player player;

    protected MixinEnchantmentMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Override
    public CraftEnchantmentView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventoryEnchanting inventory = new CraftInventoryEnchanting(this.enchantSlots);
        bukkitEntity = new CraftEnchantmentView(this.player, inventory, (EnchantmentMenu) (Object) this);
        return bukkitEntity;
    }
    // CraftBukkit end

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("RETURN"))
    public void silkard$init(int containerId, Inventory inventory, ContainerLevelAccess access, CallbackInfo ci) {
        this.player = (org.bukkit.entity.Player) inventory.player.getBukkitEntity();
        // CraftBukkit - set location on anonymous enchantSlots container
        ((ContextLocationContainer) this.enchantSlots).silkard$setLocation(access.getLocation());
    }

    @Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
    public void silkard$stillValid(net.minecraft.world.entity.player.Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!silkard$checkReachable()) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "slotsChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEnchantable()Z"))
    private boolean silkard$isEnchantable(net.minecraft.world.item.ItemStack itemstack) {
        return true;
    }

    @Redirect(method = "lambda$slotsChanged$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/EnchantmentMenu;broadcastChanges()V"))
    private void silkard$slotsChanged$broadcastChanges(EnchantmentMenu menu) {
        // CraftBukkit start - PrepareItemEnchantEvent
        net.minecraft.world.item.ItemStack itemstack = this.enchantSlots.getItem(0);
        org.bukkit.craftbukkit.inventory.CraftItemStack item = CraftItemStack.asCraftMirror(itemstack);
        EnchantmentOffer[] offers = new EnchantmentOffer[3];
        // access idmap needs registry access - use player's level
        net.minecraft.world.level.Level level = this.player.getWorld() != null ?
            ((org.bukkit.craftbukkit.CraftWorld) this.player.getWorld()).getHandle() : null;

        if (level != null) {
            IdMap<Holder<Enchantment>> idmap = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).asHolderIdMap();
            for (int j = 0; j < 3; ++j) {
                org.bukkit.enchantments.Enchantment enchantment = (this.enchantClue[j] >= 0) ? CraftEnchantment.minecraftHolderToBukkit(idmap.byId(this.enchantClue[j])) : null;
                offers[j] = (enchantment != null) ? new EnchantmentOffer(enchantment, this.levelClue[j], this.costs[j]) : null;
            }
        }

        PrepareItemEnchantEvent event = new PrepareItemEnchantEvent(player, this.getBukkitView(), access.getLocation().getBlock(), item, offers, level != null ? 0 : 0);
        event.setCancelled(!itemstack.isEnchantable());
        if (level != null) {
            level.getCraftServer().getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            for (int j = 0; j < 3; ++j) {
                this.costs[j] = 0;
                this.enchantClue[j] = -1;
                this.levelClue[j] = -1;
            }
        } else if (level != null) {
            IdMap<Holder<Enchantment>> idmap = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).asHolderIdMap();
            for (int j = 0; j < 3; j++) {
                EnchantmentOffer offer = event.getOffers()[j];
                if (offer != null) {
                    this.costs[j] = offer.getCost();
                    this.enchantClue[j] = idmap.getId(CraftEnchantment.bukkitToMinecraftHolder(offer.getEnchantment()));
                    this.levelClue[j] = offer.getEnchantmentLevel();
                } else {
                    this.costs[j] = 0;
                    this.enchantClue[j] = -1;
                    this.levelClue[j] = -1;
                }
            }
        }
        // CraftBukkit end

        menu.broadcastChanges();
    }

    @Inject(method = "clickMenuButton", at = @At("HEAD"), cancellable = true)
    private void silkard$clickMenuButton(net.minecraft.world.entity.player.Player player, int buttonId, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - EnchantItemEvent
        if (buttonId >= 0 && buttonId < 3) {
            net.minecraft.world.item.ItemStack itemstack = this.enchantSlots.getItem(0);
            net.minecraft.world.item.ItemStack itemstack1 = this.enchantSlots.getItem(1);

            if (!itemstack.isEmpty() && !itemstack1.isEmpty() && player.experienceLevel >= this.costs[buttonId] || player.getAbilities().instabuild) {
                net.minecraft.world.level.Level level = player.level();
                IdMap<Holder<Enchantment>> idmap = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).asHolderIdMap();

                java.util.List<EnchantmentInstance> list = this.getEnchantmentList(level.registryAccess(), itemstack, buttonId, this.costs[buttonId]);

                if (true || !list.isEmpty()) {
                    net.minecraft.world.item.ItemStack itemstack2 = itemstack;
                    Map<org.bukkit.enchantments.Enchantment, Integer> enchants = new java.util.HashMap<>();
                    for (EnchantmentInstance instance : list) {
                        enchants.put(CraftEnchantment.minecraftHolderToBukkit(instance.enchantment()), instance.level());
                    }
                    CraftItemStack item = CraftItemStack.asCraftMirror(itemstack2);

                    org.bukkit.enchantments.Enchantment hintedEnchantment = CraftEnchantment.minecraftHolderToBukkit(idmap.byId(enchantClue[buttonId]));
                    int hintedEnchantmentLevel = levelClue[buttonId];
                    EnchantItemEvent event = new EnchantItemEvent((org.bukkit.entity.Player) player.getBukkitEntity(), this.getBukkitView(), access.getLocation().getBlock(), item, this.costs[buttonId], enchants, hintedEnchantment, hintedEnchantmentLevel, buttonId);
                    level.getCraftServer().getPluginManager().callEvent(event);

                    int expLevelCost = event.getExpLevelCost();
                    if (event.isCancelled() || (expLevelCost > player.experienceLevel && !player.getAbilities().instabuild) || event.getEnchantsToAdd().isEmpty()) {
                        cir.setReturnValue(false);
                        return;
                    }

                    if (itemstack.is(net.minecraft.world.item.Items.BOOK)) {
                        itemstack2 = itemstack.transmuteCopy(net.minecraft.world.item.Items.ENCHANTED_BOOK);
                        this.enchantSlots.setItem(0, itemstack2);
                    }

                    for (Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : event.getEnchantsToAdd().entrySet()) {
                        Holder<Enchantment> nms = CraftEnchantment.bukkitToMinecraftHolder(entry.getKey());
                        if (nms == null) {
                            continue;
                        }
                        EnchantmentInstance enchantmentinstance = new EnchantmentInstance(nms, entry.getValue());
                        itemstack2.enchant(enchantmentinstance.enchantment(), enchantmentinstance.level());
                    }

                    player.onEnchantmentPerformed(itemstack, expLevelCost);
                    itemstack1.consume(expLevelCost, player);
                    if (itemstack1.isEmpty()) {
                        this.enchantSlots.setItem(1, net.minecraft.world.item.ItemStack.EMPTY);
                    }

                    this.slotsChanged(this.enchantSlots);
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
        cir.setReturnValue(false);
        // CraftBukkit end
    }

    @Shadow
    private java.util.List<EnchantmentInstance> getEnchantmentList(net.minecraft.core.RegistryAccess registryAccess, net.minecraft.world.item.ItemStack stack, int buttonId, int cost) {
        throw new UnsupportedOperationException();
    }
}
