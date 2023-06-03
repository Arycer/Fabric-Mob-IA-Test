package me.arycer.eufoniatestai.goal.bird;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class AttackTargetGoal extends Goal {
    protected final PathAwareEntity entity;
    private int delay = toGoalTicks(20);
    private int attackDelay = toGoalTicks(5);

    public AttackTargetGoal(PathAwareEntity entity) {
        this.entity = entity;
    }


    @Override
    public boolean canStart() {
        if (this.delay > 0) {
            --this.delay;
            return false;
        }

        this.delay = toGoalTicks(20);

        LivingEntity target = entity.getTarget();
        return target != null && entity.isTarget(target, TargetPredicate.DEFAULT);
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = entity.getTarget();
        return target != null && entity.isTarget(target, TargetPredicate.DEFAULT);
    }

    @Override
    public void tick() {
        LivingEntity target = entity.getTarget();
        if (target == null || !entity.isTarget(target, TargetPredicate.DEFAULT)) return;

        entity.getLookControl().lookAt(target.getPos());

        Vec3d targetPos = target.getEyePos();
        Vec3d velocity = targetPos.subtract(entity.getPos()).normalize().multiply(getSpeedMod());
        entity.setVelocity(velocity);

        if (entity.squaredDistanceTo(target.getPos()) < 2) {
            entity.setVelocity(Vec3d.ZERO);
            if (attackDelay > 0) {
                --attackDelay;
                return;
            }

            if (entity.getBoundingBox().expand(0.5).intersects(target.getBoundingBox())) {
                entity.tryAttack(target);
                attackDelay = toGoalTicks(5);
            }
        }
    }

    private double getSpeedMod() {
        return 0.5 + entity.getRandom().nextDouble() * 0.4;
    }
}
