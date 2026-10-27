package com.mohistmc.silkard.mixin.vanilla.world.food;

import com.mohistmc.silkard.injected.world.food.ContextFoodData;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/5/1 18:37
 */
@Mixin(FoodData.class)
public class MixinFoodData implements ContextFoodData {

    @Shadow public int foodLevel;
    @Shadow public float saturationLevel;
    @Shadow public float exhaustionLevel;
    @Shadow private int tickTimer;

    @Shadow
    private void add(int food, float saturation) {}

    // CraftBukkit start
    public int saturatedRegenRate = 10;
    public int unsaturatedRegenRate = 80;
    public int starvationRate = 80;
    // CraftBukkit end

    @Override
    public int silkard$saturatedRegenRate() {
        return saturatedRegenRate;
    }

    @Override
    public void silkard$saturatedRegenRate(int saturatedRegenRate) {
        this.saturatedRegenRate = saturatedRegenRate;
    }

    @Override
    public int silkard$unsaturatedRegenRate() {
        return unsaturatedRegenRate;
    }

    @Override
    public void silkard$unsaturatedRegenRate(int unsaturatedRegenRate) {
        this.unsaturatedRegenRate = unsaturatedRegenRate;
    }

    @Override
    public int silkard$starvationRate() {
        return starvationRate;
    }

    @Override
    public void silkard$starvationRate(int starvationRate) {
        this.starvationRate = starvationRate;
    }

    @Override
    public void eat(FoodProperties foodproperties, ItemStack itemstack, ServerPlayer serverplayer) {
        int oldFoodLevel = foodLevel;

        FoodLevelChangeEvent event = CraftEventFactory.callFoodLevelChangeEvent(serverplayer, foodproperties.nutrition() + oldFoodLevel, itemstack);

        if (!event.isCancelled()) {
            this.add(event.getFoodLevel() - oldFoodLevel, foodproperties.saturation());
        }

        serverplayer.getBukkitEntity().sendHealthUpdate();
    }

    @Overwrite
    public void tick(ServerPlayer player) {
        ServerLevel level = player.level();
        Difficulty difficulty = level.getDifficulty();
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                FoodLevelChangeEvent event = CraftEventFactory.callFoodLevelChangeEvent(player, Math.max(this.foodLevel - 1, 0));
                if (!event.isCancelled()) {
                    this.foodLevel = event.getFoodLevel();
                }
                player.connection.send(new ClientboundSetHealthPacket(player.getBukkitEntity().getScaledHealth(), this.foodLevel, this.saturationLevel));
            }
        }

        boolean naturalRegen = level.getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION);
        if (naturalRegen && this.saturationLevel > 0.0F && player.isHurt() && this.foodLevel >= 20) {
            ++this.tickTimer;
            if (this.tickTimer >= this.saturatedRegenRate) {
                float saturationSpent = Math.min(this.saturationLevel, 6.0F);
                player.heal(saturationSpent / 6.0F, EntityRegainHealthEvent.RegainReason.SATIATED);
                player.causeFoodExhaustion(saturationSpent, EntityExhaustionEvent.ExhaustionReason.REGEN);
                this.tickTimer = 0;
            }
        } else if (naturalRegen && this.foodLevel >= 18 && player.isHurt()) {
            ++this.tickTimer;
            if (this.tickTimer >= this.unsaturatedRegenRate) {
                player.heal(1.0F, EntityRegainHealthEvent.RegainReason.SATIATED);
                player.causeFoodExhaustion(6.0F, EntityExhaustionEvent.ExhaustionReason.REGEN);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= this.starvationRate) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.hurtServer(level, player.damageSources().starve(), 1.0F);
                }
                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }
    }
}
