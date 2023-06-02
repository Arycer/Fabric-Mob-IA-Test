package me.arycer.eufoniatestai.goal;

import me.arycer.eufoniatestai.Main;
import me.arycer.eufoniatestai.poi.PointsOfInterest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.HashMap;

public class CutWoodGoal extends Goal {
    protected final MobEntity entity;
    private BlockPos woodPos;
    private long lastChecked;
    private boolean finished;
    private int breakProgress;
    private BlockState prevBlockState;

    public CutWoodGoal(MobEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        ServerWorld world = (ServerWorld) entity.getWorld();
        if (world.getTime() - lastChecked < 300) return false;
        Main.LOGGER.info(String.format("CutWoodGoal: %s searching wood", entity.getName().getString()));
        lastChecked = world.getTime();

        if (!needsWood()) return false;

        LivingEntity target = entity.getTarget();
        if (target != null) return false;

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
        Main.LOGGER.info(String.format("CutWoodGoal: %s found wood!", entity.getName().getString()));

        if (!entity.canPickUpLoot()) entity.setCanPickUpLoot(true);

        prevBlockState = entity.getWorld().getBlockState(woodPos);
        entity.getNavigation().startMovingTo(woodPos.getX(), woodPos.getY(), woodPos.getZ(), 1);
    }

    @Override
    public void stop() {
        Main.LOGGER.info("CutWoodGoal stopped");
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
                entity.getEyePos(),
                woodPos.toCenterPos(),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        ));

        BlockState hitBlockState = world.getBlockState(hitResult.getBlockPos());
        if (hitBlockState == null) return;

        breakProgress++;
        world.setBlockBreakingInfo(this.entity.getId(), hitResult.getBlockPos(), breakProgress);
        if (breakProgress < getMaxProgress(hitBlockState.getBlock())) return;

        world.breakBlock(hitResult.getBlockPos(), true, entity);
        finished = true;
    }

    private ItemStack getBlocksItemStack() {
        ItemStack mainHandStack = this.entity.getMainHandStack();
        ItemStack offHandStack = this.entity.getOffHandStack();

        if (mainHandStack.isEmpty() && offHandStack.isEmpty()) return null;

        if (mainHandStack.getItem() instanceof BlockItem) return mainHandStack;
        if (offHandStack.getItem() instanceof BlockItem) return offHandStack;

        return null;
    }

    private Hand getHand(MobEntity entity) {
        ItemStack mainHandStack = entity.getMainHandStack();
        ItemStack offHandStack = entity.getOffHandStack();

        if (mainHandStack.isEmpty() && offHandStack.isEmpty()) return null;

        if (mainHandStack.getItem() instanceof BlockItem) return Hand.MAIN_HAND;
        if (offHandStack.getItem() instanceof BlockItem) return Hand.OFF_HAND;

        return null;
    }

    private boolean farmed;

    private boolean needsWood() {
        ItemStack blocksItemStack = getBlocksItemStack();
        if (blocksItemStack == null) {
            farmed = false;
            return true;
        }

        if (blocksItemStack.getCount() >= 16) {
            craftPlanks();
            return false;
        }

        return !farmed;
    }

    HashMap<Block, Block> blockToPlanks = new HashMap<>(){{
        put(Blocks.OAK_LOG, Blocks.OAK_PLANKS);
        put(Blocks.SPRUCE_LOG, Blocks.SPRUCE_PLANKS);
        put(Blocks.BIRCH_LOG, Blocks.BIRCH_PLANKS);
        put(Blocks.JUNGLE_LOG, Blocks.JUNGLE_PLANKS);
        put(Blocks.ACACIA_LOG, Blocks.ACACIA_PLANKS);
        put(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS);
    }};

    private void craftPlanks() {
        ItemStack blocksItemStack = getBlocksItemStack();
        if (blocksItemStack == null) return;

        Item item = blocksItemStack.getItem();
        if (!(item instanceof BlockItem blockItem)) return;

        Block block = blockItem.getBlock();
        if (block == null) return;

        if (!blockToPlanks.containsKey(block)) return;

        int count = blocksItemStack.getCount() * 4;
        ItemStack planksItemStack = new ItemStack(blockToPlanks.get(block), count);
        entity.setStackInHand(getHand(entity), planksItemStack);
        farmed = true;
    }

    private int getMaxProgress(Block block) {
        return (int) (block.getHardness() * 10);
    }
}
