package com.dayofpi.lucys_sloths.common;

import com.dayofpi.lucys_sloths.LucysSloths;
import com.dayofpi.lucys_sloths.registry.ModEntities;
import com.dayofpi.lucys_sloths.registry.ModSounds;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SlothEntity extends TamableAnimal {
    public static final String TWO_TOED = "two_toed";
    public static final String THREE_TOED = "three_toed";

    /** Items con los que se doma al perezoso (editable por datapack). */
    public static final TagKey<net.minecraft.world.item.Item> TAMING_ITEMS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(LucysSloths.MODID, "taming_items"));

    public final AnimationState idlingAnimationState = new AnimationState();
    public final AnimationState crawlingAnimationState = new AnimationState();
    public final AnimationState idlingInWaterAnimationState = new AnimationState();
    public final AnimationState swimmingAnimationState = new AnimationState();
    public final AnimationState sittingAnimationState = new AnimationState();

    private static final EntityDataAccessor<String> TYPE =
            SynchedEntityData.defineId(SlothEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Optional<BlockState>> PLANT_BLOCK =
            SynchedEntityData.defineId(SlothEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
    private static final EntityDataAccessor<Boolean> IS_ATTACHED =
            SynchedEntityData.defineId(SlothEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<BlockPos> ATTACHED_POS =
            SynchedEntityData.defineId(SlothEntity.class, EntityDataSerializers.BLOCK_POS);

    private final List<Block> naturalPlants =
            List.of(Blocks.SHORT_GRASS, Blocks.FERN, Blocks.OAK_SAPLING, Blocks.JUNGLE_SAPLING);

    public SlothEntity(EntityType<? extends SlothEntity> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
    }

    public static AttributeSupplier.Builder createSlothAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.1D);
    }

    // ---------------------------------------------------------------- spawn

    public static boolean canSpawn(EntityType<SlothEntity> type, ServerLevelAccessor level, MobSpawnType reason,
                                   BlockPos pos, RandomSource random) {
        return isBlockValidForSpawn(pos.below(), level) && Animal.isBrightEnoughToSpawn(level, pos);
    }

    private static boolean isBlockValidForSpawn(BlockPos pos, ServerLevelAccessor level) {
        BlockState state = level.getBlockState(pos);
        return state.is(BlockTags.LOGS) || state.is(BlockTags.DIRT);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnGroupData) {
        this.setSlothType(this.random.nextBoolean() ? TWO_TOED : THREE_TOED);
        if (this.isBaby()) {
            return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        }
        if (this.random.nextInt(6) == 0) {
            this.setPlantBlock(this.naturalPlants.get(this.random.nextInt(this.naturalPlants.size() - 1)).defaultBlockState());
        } else if (this.random.nextInt(6) == 0) {
            SlothEntity baby = ModEntities.SLOTH.get().create(level.getLevel());
            if (baby != null) {
                baby.setAge(-24000);
                spawnGroupData = this.initializeRider(level, difficulty, baby);
            }
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private SpawnGroupData initializeRider(ServerLevelAccessor level, DifficultyInstance difficulty, Mob rider) {
        rider.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
        rider.finalizeSpawn(level, difficulty, MobSpawnType.JOCKEY, null);
        rider.startRiding(this, true);
        return new AgeableMob.AgeableMobGroupData(0.0F);
    }

    // ---------------------------------------------------------------- sonidos

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.SLOTH_STEP.get(), 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SLOTH_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SLOTH_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SLOTH_HURT.get();
    }

    // ---------------------------------------------------------------- IA

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.1D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Ocelot.class, 10.0F, 1.0D, 1.1D));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.1D, Ingredient.of(ItemTags.LEAVES), false));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.1D, 6.0F, 2.0F));
        this.goalSelector.addGoal(7, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(8, new ClimbParentGoal(this));
        this.goalSelector.addGoal(9, new SlothWanderGoal(this, 0.9D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 4.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, SlothEntity.class, 4.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        if (level.getBlockState(pos.below()).is(BlockTags.LOGS) || level.getBlockState(pos.above()).is(BlockTags.LEAVES)) {
            return 10.0F;
        }
        if (level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)) {
            return 5.0F;
        }
        return level.getPathfindingCostFromLightLevels(pos);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.LEAVES);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        SlothEntity baby = ModEntities.SLOTH.get().create(level);
        if (baby != null && otherParent instanceof SlothEntity other) {
            baby.setSlothType(this.random.nextBoolean() ? this.getSlothType() : other.getSlothType());
        }
        return baby;
    }

    // ---------------------------------------------------------------- datos

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TYPE, TWO_TOED);
        builder.define(PLANT_BLOCK, Optional.empty());
        builder.define(IS_ATTACHED, false);
        builder.define(ATTACHED_POS, BlockPos.ZERO);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Type", this.getSlothType());
        BlockState plant = this.getPlantBlock();
        if (plant != null) {
            tag.put("plantBlockState", NbtUtils.writeBlockState(plant));
        }
        tag.putBoolean("IsAttached", this.isAttached());
        BlockPos pos = this.getAttachedPos();
        tag.putInt("AttachedX", pos.getX());
        tag.putInt("AttachedY", pos.getY());
        tag.putInt("AttachedZ", pos.getZ());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Type", 8)) {
            this.setSlothType(tag.getString("Type"));
        }
        BlockState plant = null;
        if (tag.contains("plantBlockState", 10)) {
            plant = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tag.getCompound("plantBlockState"));
            if (plant.isAir()) plant = null;
        }
        this.setIsAttached(tag.getBoolean("IsAttached"));
        this.setAttachedPos(new BlockPos(tag.getInt("AttachedX"), tag.getInt("AttachedY"), tag.getInt("AttachedZ")));
        this.setPlantBlock(plant);
    }

    public String getSlothType() {
        return this.entityData.get(TYPE);
    }

    private void setSlothType(String type) {
        this.entityData.set(TYPE, type);
    }

    @Nullable
    public BlockState getPlantBlock() {
        return this.entityData.get(PLANT_BLOCK).orElse(null);
    }

    public void setPlantBlock(@Nullable BlockState state) {
        this.entityData.set(PLANT_BLOCK, Optional.ofNullable(state));
    }

    public BlockPos getAttachedPos() {
        return this.entityData.get(ATTACHED_POS);
    }

    public void setAttachedPos(BlockPos pos) {
        this.entityData.set(ATTACHED_POS, pos);
    }

    public boolean isAttached() {
        return this.entityData.get(IS_ATTACHED);
    }

    public void setIsAttached(boolean attached) {
        this.entityData.set(IS_ATTACHED, attached);
    }

    // ---------------------------------------------------------------- planta / agua

    @Override
    protected void doWaterSplashEffect() {
        super.doWaterSplashEffect();
        if (this.getPlantBlock() != null || this.isBaby() || this.level().isClientSide) return;
        if (this.random.nextFloat() > 0.9F) {
            this.playSound(ModSounds.SLOTH_GROW_SEAGRASS.get(), 1.0F, this.getVoicePitch());
            this.setPlantBlock(Blocks.SEAGRASS.defaultBlockState());
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        BlockState plant = this.getPlantBlock();
        if (plant != null) {
            this.spawnAtLocation(plant.getBlock());
        }
    }

    private BlockState getSaplingFromStack(ItemStack stack) {
        if (stack.is(Items.SPRUCE_LEAVES)) return Blocks.SPRUCE_SAPLING.defaultBlockState();
        if (stack.is(Items.BIRCH_LEAVES)) return Blocks.BIRCH_SAPLING.defaultBlockState();
        if (stack.is(Items.JUNGLE_LEAVES)) return Blocks.JUNGLE_SAPLING.defaultBlockState();
        if (stack.is(Items.ACACIA_LEAVES)) return Blocks.ACACIA_SAPLING.defaultBlockState();
        if (stack.is(Items.DARK_OAK_LEAVES)) return Blocks.DARK_OAK_SAPLING.defaultBlockState();
        if (stack.is(Items.CHERRY_LEAVES)) return Blocks.CHERRY_SAPLING.defaultBlockState();
        if (stack.is(Items.AZALEA_LEAVES)) return Blocks.AZALEA.defaultBlockState();
        if (stack.is(Items.FLOWERING_AZALEA_LEAVES)) return Blocks.FLOWERING_AZALEA.defaultBlockState();
        if (stack.is(Items.MANGROVE_LEAVES)) return Blocks.MANGROVE_PROPAGULE.defaultBlockState();
        return Blocks.OAK_SAPLING.defaultBlockState();
    }

    // ---------------------------------------------------------------- interacción

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 1) Tijeras: quitar la planta
        if (stack.is(Items.SHEARS)) {
            if (!this.level().isClientSide && this.getPlantBlock() != null) {
                this.level().playSound(null, this, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
                this.spawnAtLocation(this.getPlantBlock().getBlock());
                this.setPlantBlock(null);
                this.gameEvent(GameEvent.SHEAR, player);
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        // 2) Domar con bayas
        if (!this.isTame() && stack.is(TAMING_ITEMS)) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(ModSounds.SLOTH_EAT.get(), 1.0F, 1.0F);
                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.setOrderedToSit(true);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // 3) Comida normal (hojas): crianza / crecer una planta
        ItemStack eaten = stack.copy();
        InteractionResult result = super.mobInteract(player, hand);
        if (result.consumesAction() && this.isFood(eaten)) {
            this.playSound(ModSounds.SLOTH_EAT.get(), 1.0F, 1.0F);
            if (!this.isBaby() && this.getPlantBlock() == null && this.random.nextBoolean() && !this.level().isClientSide) {
                this.level().broadcastEntityEvent(this, (byte) 38);
                this.playSound(SoundEvents.BONE_MEAL_USE, 1.0F, 1.0F);
                this.setPlantBlock(this.getSaplingFromStack(eaten));
            }
            return result;
        }

        // 4) Perezoso domado: sentar / levantar, o subirse a la cabeza con shift
        if (this.isTame()) {
            if (this.isOwnedBy(player)) {
                if (!player.isSecondaryUseActive()) {
                    if (!this.level().isClientSide) {
                        this.setOrderedToSit(!this.isOrderedToSit());
                        this.jumping = false;
                        this.navigation.stop();
                        this.setTarget(null);
                    }
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
                if (!this.level().isClientSide) {
                    this.setOrderedToSit(false);
                    this.startRiding(player);
                    this.refreshDimensions();
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            return result;
        }

        // 5) Perezoso salvaje: subirse a la cabeza (comportamiento original)
        if (!player.isSecondaryUseActive()) {
            this.startRiding(player);
            this.refreshDimensions();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return result;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 38) {
            this.spawnParticlesAround();
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void spawnParticlesAround() {
        for (int i = 0; i < 7; i++) {
            double dx = this.random.nextGaussian() * 0.01D;
            double dy = this.random.nextGaussian() * 0.01D;
            double dz = this.random.nextGaussian() * 0.01D;
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.2D,
                    this.getRandomZ(1.0D), dx, dy, dz);
        }
    }

    // ---------------------------------------------------------------- montar

    public boolean isRidingPlayer() {
        return this.getVehicle() instanceof Player;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
        double factor = this.getSlothType().equals(THREE_TOED) ? 0.7D : 0.5D;
        return new Vec3(0.0D, dimensions.height() * 0.75D * factor * scale, 0.0D);
    }

    @Override
    public Vec3 getVehicleAttachmentPoint(Entity vehicle) {
        // En la cabeza/hombro del jugador (en 1.19 era un desplazamiento de -0.5 sobre 0.75 de la altura)
        if (vehicle instanceof Player) {
            return new Vec3(0.0D, 0.95D, 0.0D);
        }
        return super.getVehicleAttachmentPoint(vehicle);
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (this.getVehicle() instanceof SlothEntity) {
            this.stopRiding();
        }
    }

    @Override
    public boolean onClimbable() {
        return super.onClimbable() || this.isAttached();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Entity vehicle = this.getVehicle();
        if (vehicle instanceof SlothEntity parent && parent.getPlantBlock() != null) {
            this.stopRiding();
            this.refreshDimensions();
        }
        if (vehicle instanceof Player player) {
            this.setYRot(player.getYRot());
            if (player.isShiftKeyDown()) {
                this.stopRiding();
                this.refreshDimensions();
            }
        }
    }

    // ---------------------------------------------------------------- movimiento / animaciones

    private boolean shouldWalk() {
        return this.onGround() && this.walkAnimation.speed() != 0.0F && !this.isInWater();
    }

    private boolean shouldSwim() {
        return this.walkAnimation.speed() != 0.0F && this.isInWater();
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.1F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.updateAnimations();
            return;
        }
        if (!this.isAlive()) return;

        for (BlockPos pos : BlockPos.betweenClosedStream(this.getBoundingBox().inflate(0.5D, 0.0D, 0.5D)).toList()) {
            if (this.level().getBlockState(pos).is(BlockTags.LOGS)) {
                this.setAttachedPos(pos.immutable());
                this.setIsAttached(true);
            }
        }
        if (this.isAttached()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(1.1D));
        } else if (this.distanceToSqr(Vec3.atCenterOf(this.getAttachedPos())) > 2.0D
                || !this.level().getBlockState(this.getAttachedPos()).is(BlockTags.LOGS)) {
            this.setIsAttached(false);
        }
    }

    private void updateAnimations() {
        if (this.isInSittingPose()) {
            this.sittingAnimationState.startIfStopped(this.tickCount);
            this.crawlingAnimationState.stop();
            this.idlingAnimationState.stop();
            this.swimmingAnimationState.stop();
            this.idlingInWaterAnimationState.stop();
            return;
        }
        this.sittingAnimationState.stop();

        if (this.shouldWalk()) {
            this.crawlingAnimationState.startIfStopped(this.tickCount);
        } else {
            this.crawlingAnimationState.stop();
        }

        if (this.shouldSwim()) {
            this.idlingInWaterAnimationState.stop();
            this.swimmingAnimationState.startIfStopped(this.tickCount);
        } else if (this.isInWater()) {
            this.swimmingAnimationState.stop();
            this.idlingInWaterAnimationState.startIfStopped(this.tickCount);
        } else {
            if (!this.shouldWalk()) {
                this.idlingAnimationState.startIfStopped(this.tickCount);
            } else {
                this.idlingAnimationState.stop();
            }
            this.swimmingAnimationState.stop();
            this.idlingInWaterAnimationState.stop();
        }
    }
}
