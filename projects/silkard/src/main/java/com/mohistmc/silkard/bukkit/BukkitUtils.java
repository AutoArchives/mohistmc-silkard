package com.mohistmc.silkard.bukkit;

import com.mohistmc.silkard.injected.world.level.ContextLevel;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.TreeType;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTransformEvent;

/**
 * @author Mgazul
 * @date 2026/4/2 14:56
 */
public class BukkitUtils {

    public static long pluginTimeout = 0L;

    public static MinecraftServer getServer() {
        return (Bukkit.getServer() instanceof CraftServer) ? ((CraftServer) Bukkit.getServer()).getServer() : null;
    }

    public static RegistryAccess getDefaultRegistryAccess() {
        return CraftRegistry.getMinecraftRegistry();
    }

    public static List getHumansInRange(Level level, BlockPos worldPosition, int i) {
        {
            double d0 = (double) (i * 10 + 10);

            AABB aabb = (new AABB(worldPosition)).inflate(d0).expandTowards(0.0D, (double) level.getHeight(), 0.0D);
            List<Player> list = level.<Player>getEntitiesOfClass(Player.class, aabb);

            return list;
        }
    }

    // CraftBukkit start
    public static void reload(MinecraftServer minecraftserver) {
        PackRepository packrepository = minecraftserver.getPackRepository();
        WorldData worlddata = minecraftserver.getWorldData();
        Collection<String> collection = packrepository.getSelectedIds();
        Collection<String> collection1 = ReloadCommand.discoverNewPacks(packrepository, worlddata, collection);
        minecraftserver.reloadResources(collection1);
    }
    // CraftBukkit end

    public static int getRange(List<BlockPos> effectBlocks) {
        // CraftBukkit end
        int i = effectBlocks.size();
        int j = i / 7 * 16;
        // CraftBukkit start
        return j;
    }

    public static void updateAndAttackTarget(final ServerLevel level, final BlockPos worldPosition, final BlockState blockState, final ConduitBlockEntity entity, final boolean isActive, boolean damageTarget) {
        EntityReference<LivingEntity> entityreference = ConduitBlockEntity.updateDestroyTarget(entity.destroyTarget, level, worldPosition, isActive);
        LivingEntity livingentity = EntityReference.getLivingEntity(entityreference, level);

        // CraftBukkit start
        if (damageTarget && livingentity != null) {
            if (livingentity.hurtServer(level, level.damageSources().magic().directBlock(level, worldPosition), 4.0F)) {
                level.playSound((Entity) null, livingentity.getX(), livingentity.getY(), livingentity.getZ(), SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            // CraftBukkit end
        }

        if (!Objects.equals(entityreference, entity.destroyTarget)) {
            entity.destroyTarget = entityreference;
            level.sendBlockUpdated(worldPosition, blockState, blockState, 2);
        }
    }

    public static AABB createBoundingBox(BlockPos blockPos, Direction direction, boolean hasFramedMap) {
        float shiftToBlockWall = 0.46875F;
        Vec3 position = Vec3.atCenterOf(blockPos).relative(direction, -0.46875);
        float width = hasFramedMap ? 1.0F : 0.75F;
        float height = hasFramedMap ? 1.0F : 0.75F;
        Direction.Axis axis = direction.getAxis();
        double xSize = axis == Direction.Axis.X ? 0.0625 : width;
        double ySize = axis == Direction.Axis.Y ? 0.0625 : height;
        double zSize = axis == Direction.Axis.Z ? 0.0625 : width;
        return AABB.ofSize(position, xSize, ySize, zSize);
    }

    public static AABB calculateBoundingBoxStatic(BlockPos pos, Direction direction, int width, int height) {
        // CraftBukkit end
        float f = 0.46875F;
        Vec3 vec3 = Vec3.atCenterOf(pos).relative(direction, -0.46875D);
        // CraftBukkit start
        double d0 = offsetForPaintingSize(width);
        double d1 = offsetForPaintingSize(height);
        // CraftBukkit end
        Direction direction1 = direction.getCounterClockWise();
        Vec3 vec31 = vec3.relative(direction1, d0).relative(Direction.UP, d1);
        Direction.Axis direction_axis = direction.getAxis();
        // CraftBukkit start
        double d2 = direction_axis == Direction.Axis.X ? 0.0625D : (double) width;
        double d3 = (double) height;
        double d4 = direction_axis == Direction.Axis.Z ? 0.0625D : (double) width;
        // CraftBukkit end

        return AABB.ofSize(vec31, d2, d3, d4);
    }

    private static double offsetForPaintingSize(int size) {
        return size % 2 == 0 ? 0.5 : 0.0;
    }

    public static Ingredient ofStacks(List<ItemStack> stacks) {
        Ingredient recipe = Ingredient.of(stacks.stream().map(ItemStack::getItem));
        recipe.silkard$itemStacks(stacks);
        return recipe;
    }

    public static ZombieVillager convertVillagerToZombieVillager(ServerLevel level, Villager villager, net.minecraft.core.BlockPos blockPosition, boolean silent, EntityTransformEvent.TransformReason transformReason, CreatureSpawnEvent.SpawnReason spawnReason) {
        // CraftBukkit end
        ZombieVillager zombievillager = (ZombieVillager) villager.convertTo(EntityTypes.ZOMBIE_VILLAGER, ConversionParams.single(villager, true, true), (zombievillager1) -> {
            zombievillager1.finalizeSpawn(level, level.getCurrentDifficultyAt(zombievillager1.blockPosition()), EntitySpawnReason.CONVERSION, new Zombie.ZombieGroupData(false, true));
            zombievillager1.setVillagerData(villager.getVillagerData());
            zombievillager1.setGossips(villager.getGossips().copy());
            zombievillager1.setTradeOffers(villager.getOffers().copy());
            zombievillager1.setVillagerXp(villager.getVillagerXp());
            // CraftBukkit start
            if (!silent) {
                level.levelEvent((Entity) null, 1026, blockPosition, 0);
            }

        }, transformReason, spawnReason);

        return zombievillager;
        // CraftBukkit end
    }

    public static FallingBlockEntity fall(Level level, BlockPos pos, BlockState state, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason spawnReason) {
        // CraftBukkit end
        FallingBlockEntity fallingblockentity = new FallingBlockEntity(level, (double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, state.hasProperty(BlockStateProperties.WATERLOGGED) ? (BlockState) state.setValue(BlockStateProperties.WATERLOGGED, false) : state);
        if (!CraftEventFactory.callEntityChangeBlockEvent(fallingblockentity, pos, state.getFluidState().createLegacyBlock())) return fallingblockentity; // CraftBukkit

        level.setBlock(pos, state.getFluidState().createLegacyBlock(), 3);
        level.addFreshEntity(fallingblockentity, spawnReason); // CraftBukkit
        return fallingblockentity;
    }

    public static InteractionResult applyBonemeal(UseOnContext context) {
        // CraftBukkit end
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(context.getClickedFace());
        ItemStack itemstack = context.getItemInHand();

        if (BoneMealItem.growCrop(itemstack, level, blockpos)) {
            if (!level.isClientSide()) {
                if (context.getPlayer() != null) itemstack.causeUseVibration(context.getPlayer(), GameEvent.ITEM_INTERACT_FINISH); // CraftBukkit - SPIGOT-7518
                level.levelEvent(1505, blockpos, 15);
                return InteractionResult.SUCCESS_SERVER;
            } else {
                return InteractionResult.PASS;
            }
        } else {
            BlockState blockstate = level.getBlockState(blockpos);
            boolean flag = blockstate.isFaceSturdy(level, blockpos, context.getClickedFace());

            if (flag && BoneMealItem.growWaterPlant(itemstack, level, blockpos1, context.getClickedFace())) {
                if (!level.isClientSide()) {
                    if (context.getPlayer() != null) itemstack.causeUseVibration(context.getPlayer(), GameEvent.ITEM_INTERACT_FINISH); // CraftBukkit - SPIGOT-7518
                    level.levelEvent(1505, blockpos1, 15);
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public static TreeType treeType = null;

    public static void setTreeType(TreeType btreeType) {
        treeType = btreeType;
    }

    public static boolean changeCauldronLevel(Level level, BlockPos pos, BlockState newBlock, Entity entity, CauldronLevelChangeEvent.ChangeReason reason) {
        CraftBlockState newState = CraftBlockStates.getBlockState(level, pos);
        newState.setData(newBlock);

        CauldronLevelChangeEvent event = new CauldronLevelChangeEvent(
                ((ContextLevel) level).getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()),
                (entity == null) ? null : entity.getBukkitEntity(), reason, newState
        );
        ((ContextLevel) level).getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        newState.update(true);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newBlock));
        return true;
    }

    public static EntityPotionEffectEvent.Cause consumeEffectCause(ItemStack stack) {
        if (stack.is(Items.MILK_BUCKET)) {
            return EntityPotionEffectEvent.Cause.MILK;
        } else if (stack.is(Items.POTION)) {
            return EntityPotionEffectEvent.Cause.POTION_DRINK;
        }
        return EntityPotionEffectEvent.Cause.FOOD;
    }

    public static boolean lowerCauldronFillLevel(BlockState state, Level level, BlockPos pos, Entity entity, CauldronLevelChangeEvent.ChangeReason reason) {
        int i = (Integer) state.getValue(LayeredCauldronBlock.LEVEL) - 1;
        BlockState blockstate1 = i == 0 ? Blocks.CAULDRON.defaultBlockState() : (BlockState) state.setValue(LayeredCauldronBlock.LEVEL, i);
        return changeCauldronLevel(level, pos, blockstate1, entity, reason);
    }

    public static Optional<EntityType<?>> byString(String id) {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.tryParse(id));
    }
}
