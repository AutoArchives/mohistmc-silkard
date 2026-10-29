package com.mohistmc.silkard.mixin.vanilla.world.entity.monster;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(targets = "net.minecraft.world.entity.monster.Silverfish$SilverfishMergeWithStoneGoal")
public class MixinSilverfish$SilverfishMergeWithStoneGoal extends RandomStrollGoal {

    public MixinSilverfish$SilverfishMergeWithStoneGoal(PathfinderMob mob, double speed) {
        super(mob, speed);
    }

    @Shadow
    private boolean doMerge;

    @Shadow
    private Direction selectedDirection;

    @Overwrite
    public void start() {
        if (!this.doMerge) {
            super.start();
            return;
        }

        Level level = this.mob.level();
        BlockPos blockpos = BlockPos.containing(this.mob.getX(), this.mob.getY() + 0.5D, this.mob.getZ()).relative(this.selectedDirection);
        BlockState blockstate = level.getBlockState(blockpos);
        if (InfestedBlock.isCompatibleHostBlock(blockstate)) {
            if (!CraftEventFactory.callEntityChangeBlockEvent(this.mob, blockpos, InfestedBlock.infestedStateByHost(blockstate))) {
                return;
            }

            level.setBlockAndUpdate(blockpos, InfestedBlock.infestedStateByHost(blockstate));
            this.mob.spawnAnim();
            ((ContextEntity) (Object) this.mob).discard(EntityRemoveEvent.Cause.ENTER_BLOCK);
        }
    }
}
