package me.arycer.eufoniatestai.goal;

import me.arycer.eufoniatestai.Main;
import me.arycer.eufoniatestai.poi.PointsOfInterest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class CollectWoodGoal extends Goal {
    protected final MobEntity entity;
    private BlockPos woodPos;
    private long lastChecked;
    private boolean finished;
    private int breakProgress;
    private BlockState prevBlockState;

    public CollectWoodGoal(MobEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        if (entity.getNavigation().isFollowingPath()) return false;

        ServerWorld world = (ServerWorld) entity.getWorld();
        if (world.getTime() - lastChecked < 300) return false;
        Main.LOGGER.info(String.format("CollectWoodGoal: %s searching wood", entity.getName().getString()));
        lastChecked = world.getTime();

        BlockPos nearestWoodPos = PointsOfInterest.getNearestPOI(entity, PointsOfInterest.getPointOfInterest("wood"), 32);
        if (nearestWoodPos == null) return false;

        woodPos = nearestWoodPos;
        finished = false;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return woodPos != null && !finished;
    }

    @Override
    public void start() {
        if (woodPos == null) return;
        Main.LOGGER.info(String.format("CollectWoodGoal: %s found wood!", entity.getName().getString()));

        prevBlockState = entity.getWorld().getBlockState(woodPos);
        entity.getNavigation().startMovingTo(woodPos.getX(), woodPos.getY(), woodPos.getZ(), 1);
    }

    @Override
    public void stop() {
        Main.LOGGER.info("CollectWoodGoal stopped");
        lastChecked = 0;
        woodPos = null;
        finished = false;
        breakProgress = 0;
        prevBlockState = null;
    }

    @Override
    public void tick() {
        if (woodPos == null) return;
        if (entity.getNavigation().isFollowingPath()) return;

        World world = entity.getWorld();
        if (world == null) return;

        BlockState blockState = world.getBlockState(woodPos);
        if (blockState == null) return;

        if (blockState.isAir() || !blockState.equals(prevBlockState)) {
            finished = true;
            return;
        }

        entity.getLookControl().lookAt(woodPos.getX(), woodPos.getY(), woodPos.getZ());
        if (!this.entity.handSwinging) {
            this.entity.swingHand(this.entity.getActiveHand());
        }

        BlockHitResult hitResult = world.raycast(new RaycastContext(
                entity.getPos(),
                woodPos.toCenterPos(),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        ));

        BlockState hitBlockState = world.getBlockState(hitResult.getBlockPos());
        if (hitBlockState == null) return;

        breakProgress++;
        int maxProgress = getMaxProgress(hitBlockState.getBlock());
        world.setBlockBreakingInfo(this.entity.getId(), hitResult.getBlockPos(), breakProgress);

        if (breakProgress >= maxProgress) {
            world.breakBlock(hitResult.getBlockPos(), true, entity);
            finished = true;
        }
    }

    private int getMaxProgress(Block block) {
        return (int) (block.getHardness() * 10);
    }
}
