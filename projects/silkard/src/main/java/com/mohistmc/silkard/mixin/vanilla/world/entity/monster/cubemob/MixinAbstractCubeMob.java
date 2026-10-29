package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.cubemob;

import com.mohistmc.silkard.injected.world.level.ContextLevelWriter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.ConversionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CubeMobSplitEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Ports nms-patches/net/minecraft/world/entity/monster/Slime.patch.
 * The split logic moved from Slime to AbstractCubeMob in MC 26.3.
 *
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(AbstractCubeMob.class)
public abstract class MixinAbstractCubeMob extends AbstractCubeMob {

    protected MixinAbstractCubeMob(EntityType<? extends AbstractCubeMob> type, Level level) {
        super(type, level);
    }

    // CraftBukkit start - split events
    @Overwrite
    public void remove(Entity.RemovalReason reason) {
        int i = this.getSize();
        if (!this.level().isClientSide() && i > 1 && this.isDeadOrDying()) {
            float f = this.getDimensions(this.getPose()).width() / 2.0F;
            int j = i / 2;
            int k = this.getSplitCount();
            PlayerTeam playerteam = this.getTeam();
            // CraftBukkit start
            CubeMobSplitEvent event = new CubeMobSplitEvent((org.bukkit.entity.AbstractCubeMob) this.getBukkitEntity(), k);
            this.level().getCraftServer().getPluginManager().callEvent(event);

            if (!event.isCancelled() && event.getCount() > 0) {
                k = event.getCount();
            } else {
                super.remove(reason);
                return;
            }
            List<LivingEntity> slimes = new ArrayList<>(j);
            // CraftBukkit end

            for (int l = 0; l < k; ++l) {
                float f2 = ((float) (l % 2) - 0.5F) * f;
                float f3 = ((float) (l / 2) - 0.5F) * f;

                AbstractCubeMob converted = this.convertTo(this.getType(), new ConversionParams(ConversionType.SPLIT_ON_DEATH, false, false, playerteam), EntitySpawnReason.TRIGGERED, (slime) -> {
                    this.setUpSplitCube(slime, j, f2, f3);
                    // CraftBukkit start
                }, null, null);
                if (converted != null) {
                    slimes.add(converted);
                }
                // CraftBukkit end
            }
            // CraftBukkit start
            if (CraftEventFactory.callEntityTransformEvent(this, slimes, EntityTransformEvent.TransformReason.SPLIT).isCancelled()) {
                super.remove(reason);
                return;
            }
            for (LivingEntity living : slimes) {
                ((ContextLevelWriter) this.level()).addFreshEntity(living, CreatureSpawnEvent.SpawnReason.SLIME_SPLIT);
            }
            // CraftBukkit end
        }

        super.remove(reason);
    }
    // CraftBukkit end
}
