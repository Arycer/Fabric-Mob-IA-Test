package me.arycer.eufoniatestai.goal;

import me.arycer.eufoniatestai.Main;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class TowerUpGoal extends Goal {
    protected final MobEntity entity;
    protected int tickCount;

    public TowerUpGoal(MobEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        if (this.entity.getNavigation().isFollowingPath()) return false;

        BreakBlockGoal breakBlockGoal = new BreakBlockGoal(this.entity);
        if (breakBlockGoal.canStart()) return false;

        return getBlocksItemStack() != null;
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }

    @Override
    public void start() {
        this.tickCount = 10;
        towerUp();
        Main.LOGGER.info(String.format("TowerUpGoal from %s started!", this.entity.getName().getString()));
    }

    @Override
    public void stop() {
        Main.LOGGER.info(String.format("TowerUpGoal from %s stopped!", this.entity.getName().getString()));
    }

    @Override
    public void tick() {
        if (this.tickCount > 0) {
            this.tickCount--;
            return;
        }

        towerUp();
    }

    private ItemStack getBlocksItemStack() {
        ItemStack mainHandStack = this.entity.getMainHandStack();
        ItemStack offHandStack = this.entity.getOffHandStack();

        if (mainHandStack.isEmpty() && offHandStack.isEmpty()) return null;

        if (mainHandStack.getItem() instanceof BlockItem) return mainHandStack;
        if (offHandStack.getItem() instanceof BlockItem) return offHandStack;

        return null;
    }

    private Block getHandBlock() {
        ItemStack blocksItemStack = getBlocksItemStack();
        if (blocksItemStack == null) return null;

        Item item = blocksItemStack.getItem();
        if (!(item instanceof BlockItem blockItem)) return null;

        return blockItem.getBlock();
    }

    private void towerUp() {
        Block handBlock = getHandBlock();
        if (handBlock == null) return;

        LivingEntity target = this.entity.getTarget();
        if (target == null) return;

        BlockPos targetPos = target.getBlockPos();
        BlockPos entityPos = this.entity.getBlockPos();

        if (targetPos.getY() < entityPos.getY()) return;

        World world = this.entity.getWorld();

        this.entity.getJumpControl().setActive();
        this.entity.getLookControl().lookAt(entityPos.down().toCenterPos());

        world.emitGameEvent(GameEvent.BLOCK_PLACE, entityPos, GameEvent.Emitter.of(this.entity));
        world.setBlockState(entityPos, handBlock.getDefaultState());

        ItemStack blocksItemStack = getBlocksItemStack();
        if (blocksItemStack == null) return;

        blocksItemStack.decrement(1);
    }

    //TODO: Make the mob bridge
}
