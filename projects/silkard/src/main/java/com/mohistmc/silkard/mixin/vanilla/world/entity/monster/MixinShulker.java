package com.mohistmc.silkard.mixin.vanilla.world.entity.monster;

import com.mohistmc.silkard.injected.world.level.ContextLevelWriter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(Shulker.class)
public abstract class MixinShulker {

    @Shadow
    protected static EntityDataAccessor<Byte> DATA_PEEK_ID;

    @Shadow
    protected abstract Direction findAttachableSurface(BlockPos pos);

    @Shadow
    protected abstract void setAttachFace(Direction direction);

    @Overwrite
    protected boolean teleportSomewhere() {
        Entity self = (Entity) (Object) this;
        if (((Mob) (Object) this).isNoAi() || !self.isAlive()) {
            return false;
        }

        BlockPos blockpos = self.blockPosition();
        Level level = self.level();

        for (int i = 0; i < 5; i++) {
            BlockPos blockpos1 = blockpos.offset(
                    Mth.randomBetweenInclusive(self.getRandom(), -8, 8),
                    Mth.randomBetweenInclusive(self.getRandom(), -8, 8),
                    Mth.randomBetweenInclusive(self.getRandom(), -8, 8));
            if (blockpos1.getY() > level.getMinY()
                    && level.isEmptyBlock(blockpos1)
                    && level.getWorldBorder().isWithinBounds(blockpos1)
                    && level.noCollision(self, new AABB(blockpos1).deflate(1.0E-6D))) {
                Direction direction = this.findAttachableSurface(blockpos1);
                if (direction == null) {
                    continue;
                }
                if (level.getBlockState(blockpos1.relative(direction)).is(BlockTags.SHULKER_DOES_NOT_TELEPORT_TO)) {
                    continue;
                }

                EntityTeleportEvent event = CraftEventFactory.callEntityTeleportEvent(self, blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
                if (event.isCancelled()) {
                    return false;
                }
                blockpos1 = CraftLocation.toBlockPosition(event.getTo());

                ((Entity) (Object) this).unRide();
                this.setAttachFace(direction);
                self.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
                self.setPos(blockpos1.getX() + 0.5D, blockpos1.getY(), blockpos1.getZ() + 0.5D);
                level.gameEvent(GameEvent.TELEPORT, blockpos, GameEvent.Context.of(self));
                level.levelEvent(2016, blockpos, BlockUtil.packDifferenceInPosition(blockpos, self.blockPosition(), 8, 8, 8));
                ((Entity) (Object) this).getEntityData().set(DATA_PEEK_ID, (byte) 0);
                ((Mob) (Object) this).setTarget(null);
                return true;
            }
        }

        return false;
    }

    @Redirect(method = "hitByShulkerBullet", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean silkard$addShulker(Level level, Entity entity) {
        return ((ContextLevelWriter) level).addFreshEntity(entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.BREEDING);
    }
}
