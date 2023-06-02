package me.arycer.eufoniatestai.goal.bird;

import me.arycer.eufoniatestai.Main;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WanderGoal extends Goal {
    protected final PathAwareEntity entity;
    private int delay = toGoalTicks(20);

    public WanderGoal(PathAwareEntity entity) {
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
        return target == null || !entity.isTarget(target, TargetPredicate.DEFAULT) || !isMoving();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = entity.getTarget();
        return target == null || !entity.isTarget(target, TargetPredicate.DEFAULT) || !isMoving();
    }

    @Override
    public void tick() {
        if (delay > 0) {
            --delay;
            return;
        }

        LivingEntity target = entity.getTarget();
        if (target != null && entity.isTarget(target, TargetPredicate.DEFAULT)) return;

        Vec3d targetPos = getWanderTarget();
        Vec3d velocity = targetPos.subtract(entity.getPos()).normalize().multiply(0.7);

        entity.getLookControl().lookAt(targetPos);
        entity.setVelocity(velocity);

        delay = toGoalTicks(2);
    }

    private Vec3d getWanderTarget() {
        Vec3d pos = entity.getPos();

        int radius = 5;
        double random = entity.getRandom().nextDouble() * radius * Math.PI;

        double i = Math.cos(random); // x
        double j = Math.sin(random); // z

        int kOffset = 2;
        double k = entity.getRandom().nextDouble() * kOffset - kOffset / 2.0; // y

        BlockState state = entity.world.getBlockState(BlockPos.ofFloored(pos.add(i, k - 1, j)));
        if (!state.isAir()) k += 5;

        return pos.add(i, k, j);
    }

    private boolean isMoving() {
        return entity.getVelocity().lengthSquared() > 0.2;
    }
}
