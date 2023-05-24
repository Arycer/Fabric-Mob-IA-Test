package me.arycer.eufoniatestai.goal;

import me.arycer.eufoniatestai.Main;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class BreakBlockGoal extends Goal {
    protected final MobEntity entity;
    protected int breakProgress;
    protected BlockState blockState;
    protected BlockPos blockPos;

    public BreakBlockGoal(MobEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        if (this.entity.getNavigation().isFollowingPath()) return false;

        LivingEntity target = this.entity.getTarget();
        if (target == null) return false;

        lookAtTarget();
        return getLookingBlock() != null;
    }

    @Override
    public boolean shouldContinue() {
        if (this.entity.getNavigation().isFollowingPath()) return false;

        LivingEntity target = this.entity.getTarget();
        if (target == null) return false;

        lookAtTarget();
        if (getLookingBlock() == null) return false;
        return this.blockState == getLookingBlock();
    }

    @Override
    public void start() {
        Main.LOGGER.info(String.format("BreakBlockGoal from %s started!", this.entity.getName().getString()));
        mineBlock();
    }

    @Override
    public void stop() {
        Main.LOGGER.info(String.format("BreakBlockGoal from %s stopped!", this.entity.getName().getString()));

        this.breakProgress = 0;
        this.blockState = null;
        this.blockPos = null;
    }

    @Override
    public void tick() {
        String entityName = this.entity.getName().getString();
        int maxProgress = getMaxProgress(this.blockState.getBlock());
        Main.LOGGER.info(String.format("BreakBlockGoal from %s ticked! Break Progress: %s/%s", entityName, this.breakProgress, maxProgress));

        mineBlock();
    }

    private void lookAtTarget() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) return;

        this.entity.getLookControl().lookAt(target);
    }

    private BlockState getLookingBlock() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) return null;

        World world = this.entity.getWorld();
        BlockHitResult hitResult;

        hitResult = world.raycast(new RaycastContext(
                entity.getPos(),
                target.getPos().add(0, 1, 0),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        ));

        if (hitResult == null) {
            hitResult = world.raycast(new RaycastContext(
                    entity.getPos(),
                    target.getPos().add(0, 2, 0),
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    entity
            ));
        }

        if (hitResult == null) {
            hitResult = world.raycast(new RaycastContext(
                    entity.getPos(),
                    target.getPos(),
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    entity
            ));
        }

        if (hitResult == null) return null;

        HitResult.Type type = hitResult.getType();
        if (!type.equals(HitResult.Type.BLOCK)) return null;

        this.blockState = world.getBlockState(hitResult.getBlockPos());
        this.blockPos = hitResult.getBlockPos();
        return this.blockState;
    }

    private boolean canMineBlock() {
        World world = this.entity.getWorld();
        float hardness = blockState.getHardness(world, this.blockPos);

        ItemStack heldItem = this.entity.getMainHandStack();
        if (heldItem.isEmpty()) {
            return hardness < 1.0F;
        } else {
            return hardness < heldItem.getMiningSpeedMultiplier(blockState);
        }
    }

    private void mineBlock() {
        LivingEntity target = this.entity.getTarget();
        if (target != null) {
            this.entity.getLookControl().lookAt(target);
        }

        World world = this.entity.getWorld();
        BlockState bState = world.getBlockState(this.blockPos);
        if (!canMineBlock()) return;

        if (!this.entity.handSwinging) {
            this.entity.swingHand(this.entity.getActiveHand());
        }

        this.breakProgress++;
        int maxProgress = getMaxProgress(bState.getBlock());
        world.setBlockBreakingInfo(this.entity.getId(), this.blockPos, this.breakProgress);

        if (this.breakProgress >= maxProgress) {
            world.breakBlock(this.blockPos, true, this.entity);
            stop();
        }
    }

    private int getMaxProgress(Block block) {
        return (int) (block.getHardness() * 10);
    }

    public boolean canStartMining() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) return false;

        lookAtTarget();
        return getLookingBlock() != null && canMineBlock();
    }
}
