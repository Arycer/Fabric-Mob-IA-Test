package me.arycer.eufoniatestai.goal;

import me.arycer.eufoniatestai.Main;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class BreakBlockGoal extends Goal {
    protected final MobEntity entity;
    protected int breakProgress;
    protected Vec3d blockPos;

    public BreakBlockGoal(MobEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        if (this.entity.getNavigation().isFollowingPath()) return false;

        LivingEntity target = this.entity.getTarget();
        if (target == null) return false;

        this.blockPos = getLookingBlockPos();
        if (this.blockPos == null) return false;

        return canMine();
    }

    @Override
    public boolean shouldContinue() {
        if (this.entity.getNavigation().isFollowingPath()) return false;
        if (this.blockPos == null) return false;

        LivingEntity target = this.entity.getTarget();
        if (target == null) return false;

        Vec3d lookingBlockPos = getLookingBlockPos();
        if (lookingBlockPos == null) return false;
        if (!this.blockPos.equals(lookingBlockPos)) return false;

        return canMine();
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
        this.blockPos = null;
    }

    @Override
    public void tick() {
        World world = this.entity.getEntityWorld();
        String entityName = this.entity.getName().getString();
        int maxProgress = getMaxProgress(world.getBlockState(BlockPos.ofFloored(this.blockPos)).getBlock());
        Main.LOGGER.info(String.format("BreakBlockGoal from %s ticked! Break Progress: %s/%s", entityName, this.breakProgress, maxProgress));

        mineBlock();
    }

    private Vec3d getLookingBlockPos() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) return null;

        BlockHitResult hitResult = getHitResult(0);
        if (hitResult == null || !hitResult.getType().equals(HitResult.Type.BLOCK)) hitResult = getHitResult(1);
        if (hitResult == null|| !hitResult.getType().equals(HitResult.Type.BLOCK)) hitResult = getHitResult(-1);
        if (hitResult == null || !hitResult.getType().equals(HitResult.Type.BLOCK)) return null;

        return hitResult.getBlockPos().toCenterPos();
    }

    private BlockHitResult getHitResult(int mod) {
        LivingEntity target = this.entity.getTarget();
        if (target == null) return null;

        entity.getLookControl().lookAt(target);
        ServerWorld world = (ServerWorld) entity.getEntityWorld();

        return world.raycast(new RaycastContext(
                entity.getEyePos(),
                target.getPos().add(0, mod, 0),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        ));
    }

    private boolean canMine() {
        World world = this.entity.getWorld();
        if (!entity.getBlockPos().isWithinDistance(blockPos, 5)) return false;

        BlockState bState = world.getBlockState(BlockPos.ofFloored(this.blockPos));
        if (bState.isAir()) return false;

        float hardness = bState.getHardness(world, BlockPos.ofFloored(this.blockPos));
        if (hardness < 1) return true;

        ItemStack heldItem = this.entity.getMainHandStack();
        return heldItem.getItem() instanceof PickaxeItem;
    }

    private void mineBlock() {
        LivingEntity target = this.entity.getTarget();
        if (target != null) {
            this.entity.getLookControl().lookAt(target);
        }

        World world = this.entity.getWorld();
        BlockState bState = world.getBlockState(BlockPos.ofFloored(this.blockPos));
        if (!canMine()) return;

        if (!this.entity.handSwinging) {
            this.entity.swingHand(this.entity.getActiveHand());
        }

        this.breakProgress++;
        int maxProgress = getMaxProgress(bState.getBlock());
        world.setBlockBreakingInfo(this.entity.getId(), BlockPos.ofFloored(this.blockPos), this.breakProgress);

        if (this.breakProgress >= maxProgress) {
            world.breakBlock(BlockPos.ofFloored(this.blockPos), true, this.entity);
            stop();
        }
    }

    private int getMaxProgress(Block block) {
        return (int) (block.getHardness() * 10);
    }
}
