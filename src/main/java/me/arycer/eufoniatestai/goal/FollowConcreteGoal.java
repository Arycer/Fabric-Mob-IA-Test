package me.arycer.eufoniatestai.goal;

import me.arycer.eufoniatestai.poi.PointsOfInterest;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FollowConcreteGoal extends Goal {
    protected final MobEntity entity;

    public FollowConcreteGoal(MobEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        if (this.entity.getNavigation().isFollowingPath()) return false;

        RegistryKey<PointOfInterestType> poi = PointsOfInterest.getPointOfInterest("concrete");
        if (poi == null) return false;

        BlockPos nearestPos = PointsOfInterest.getNearestPOI(this.entity, poi, 32);
        if (nearestPos == null) {
            lastPos = null;
            currentPos = null;
            return false;
        }

        return true;
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }

    private BlockPos lastPos;
    private BlockPos currentPos;

    @Override
    public void start() {
        BlockPos nextPos = getNextPos();
        if (nextPos == null) return;

        entity.getNavigation().startMovingTo(nextPos.getX(), nextPos.getY() + 10, nextPos.getZ(), 1);
    }

    private BlockPos getNextPos() {
        Stream<BlockPos> nearestPositions = PointsOfInterest.getNearPOIs(this.entity, PointsOfInterest.getPointOfInterest("concrete"), 32);
        if (nearestPositions == null) return null;

        List<BlockPos> nearestPositionsList = new ArrayList<>(nearestPositions.toList());
        if (nearestPositionsList.size() == 0) return null;

        nearestPositionsList.removeIf(pos -> pos.equals(currentPos) || pos.equals(lastPos));

        BlockPos nearestPos = nearestPositionsList.stream().min((pos1, pos2) -> {
            double dist1 = pos1.getSquaredDistance(entity.getBlockPos());
            double dist2 = pos2.getSquaredDistance(entity.getBlockPos());
            return Double.compare(dist1, dist2);
        }).orElse(null);
        if (nearestPos == null) return null;

        lastPos = currentPos;
        currentPos = nearestPos;
        return nearestPos;
    }
}
