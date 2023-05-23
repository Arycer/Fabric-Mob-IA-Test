package me.arycer.eufoniatestai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class DodgeArrowGoal extends Goal {
    protected final MobEntity entity;
    protected final double speed;

    public DodgeArrowGoal(MobEntity entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return targetShootingBow();
    }

    @Override
    public boolean shouldContinue() {
        return targetShootingBow();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (this.entity.getNavigation().isFollowingPath()) return;
        BlockPos dodgeLocation = getDodgeLocation();
        if (dodgeLocation == null) return;

        this.entity.getNavigation().startMovingTo(dodgeLocation.getX(), dodgeLocation.getY(), dodgeLocation.getZ(), this.speed * 1.5f);
    }

    private boolean targetShootingBow() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) return false;

        ItemStack mainHandStack = target.getMainHandStack();
        ItemStack offHandStack = target.getOffHandStack();

        if (!mainHandStack.getItem().equals(Items.BOW) && !offHandStack.getItem().equals(Items.BOW)) return false;
        return target.isUsingItem();
    }

    private BlockPos getDodgeLocation() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) return null;

        BlockPos entityPos = this.entity.getBlockPos();

        int distanceToTarget = (int) this.entity.distanceTo(target);
        if (distanceToTarget < 10) {
            int runDistance = 10 - distanceToTarget;
            Vec3d targetPos = target.getPos();

            double x = entityPos.getX() - targetPos.getX();
            double z = entityPos.getZ() - targetPos.getZ();
            double angle = Math.atan2(z, x);

            double newX = entityPos.getX() + runDistance * Math.cos(angle);
            double newZ = entityPos.getZ() + runDistance * Math.sin(angle);

            return new BlockPos((int) newX, (int) target.getY(), (int) newZ);
        }

        int distance = target.getRandom().nextInt(4) + 4;
        double angle = target.getRandom().nextDouble() * 2 * Math.PI;

        double x = entityPos.getX() + distance * Math.cos(angle);
        double z = entityPos.getZ() + distance * Math.sin(angle);

        return new BlockPos((int) x, (int) target.getY(), (int) z);
    }
}
