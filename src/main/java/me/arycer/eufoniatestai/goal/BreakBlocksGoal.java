package me.arycer.eufoniatestai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class BreakBlocksGoal extends Goal {
    protected final MobEntity entity;
    protected int breakProgress;

    public BreakBlocksGoal(MobEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        return isTargetBlocked();
    }

    @Override
    public boolean shouldContinue() {
        return isTargetBlocked();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) return;

        this.entity.getLookControl().lookAt(target);

        BlockPos mineBlockPos = target.getBlockPos();
        int entity_y = (int) this.entity.getY();
        int target_y = (int) target.getY();
        if (entity_y < target_y) {
            mineBlockPos = mineBlockPos.add(0, -1, 0);
        } else if (entity_y > target_y) {
            mineBlockPos = mineBlockPos.add(0, 1, 0);
        }

        World world = this.entity.getWorld();
        BlockHitResult blockHitResult = world.raycast(new RaycastContext(
                entity.getPos(),
                mineBlockPos.toCenterPos(),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        ));

        BlockPos blockPos = blockHitResult.getBlockPos();
        if (entity.squaredDistanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ()) > 5) return;

        breakBlock(blockPos);
    }

    private void breakBlock(BlockPos blockPos) {
        World world = this.entity.getWorld();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isAir()) return;

        if (!this.entity.handSwinging) {
            this.entity.swingHand(this.entity.getActiveHand());
        }

        this.breakProgress++;
        world.setBlockBreakingInfo(entity.getId(), blockPos, this.breakProgress);

        if (this.breakProgress >= getMaxProgress(blockState.getBlock())) {
            world.breakBlock(blockPos, true);
            this.breakProgress = 0;
        }
    }

    private int getMaxProgress(Block block) {
        return (int) (block.getHardness() * 10);
    }

    private boolean isTargetBlocked() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) {
            return false;
        } else if (!target.isAlive()) {
            return false;
        } else if (entity.getNavigation().isFollowingPath()) {
            return false;
        }

        World world = this.entity.getWorld();
        BlockHitResult blockHitResult = world.raycast(new RaycastContext(
                entity.getPos(),
                target.getPos(),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        ));

        return blockHitResult.getType() != HitResult.Type.MISS && blockHitResult.getType() != HitResult.Type.ENTITY;
    }
}
