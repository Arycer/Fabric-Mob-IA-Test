package me.arycer.eufoniatestai.entity.custom;

import me.arycer.eufoniatestai.goal.bird.AttackTargetGoal;
import me.arycer.eufoniatestai.goal.bird.WanderGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BirdEntity extends PathAwareEntity implements GeoEntity, Monster {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BirdEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 60, true);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.5f)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.1f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100.0D);
    }

    protected float getOffGroundSpeed() {
        return this.getMovementSpeed() * 0.1f;
    }

    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world) {
            public boolean isValidPosition(BlockPos pos) {
                return !this.world.getBlockState(pos.down()).isAir();
            }
        };

        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(true);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected void initGoals() {
        /*
        this.goalSelector.add(5, new FlyGoal(this, 1.0D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        //this.goalSelector.add(1, new BirdAttackGoal(this, speed));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));

        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
         */

        //this.goalSelector.add(2, new MeleeAttackGoal(this, 12.0D, false));
        //this.goalSelector.add(4, new FlyGoal(this, 1.0D));
        //this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        //this.goalSelector.add(7, new LookAroundGoal(this));
        this.goalSelector.add(1, new AttackTargetGoal(this));
        this.goalSelector.add(2, new WanderGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, VillagerEntity.class, false));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "controller", this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.bird.fly", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.bird.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // test purposes
    @Override
    public void tick() {
        super.tick();
        //LivingEntity target = this.getTarget();
        //Main.LOGGER.info("Target: " + target);
    }
}
