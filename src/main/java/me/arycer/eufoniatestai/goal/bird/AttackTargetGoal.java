package me.arycer.eufoniatestai.goal.bird;

import me.arycer.eufoniatestai.Main;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class AttackTargetGoal extends Goal {
    protected final PathAwareEntity entity;
    private int delay = toGoalTicks(20);

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
            if (delay > 0) {
                --delay;
                return;
            }
            entity.tryAttack(target);
            delay = toGoalTicks(5);
        }
    }

    private double getSpeedMod() {
        LivingEntity target = entity.getTarget();
        if (target == null || !entity.isTarget(target, TargetPredicate.DEFAULT)) return 0;

        double distance = entity.squaredDistanceTo(entity.getTarget().getPos());
        double maxDistance = 10;

        return 0.2 + (distance / maxDistance) * 0.8;
    }
}
