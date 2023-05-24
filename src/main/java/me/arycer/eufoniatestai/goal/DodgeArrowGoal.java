package me.arycer.eufoniatestai.goal;

import me.arycer.eufoniatestai.Main;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class DodgeArrowGoal extends Goal {
    protected final MobEntity entity;
    protected final double speed;

    public DodgeArrowGoal(MobEntity entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return targetShootingBow();
    }

    @Override
    public boolean shouldContinue() {
        return targetShootingBow();
    }

    @Override
    public void tick() {
        if (this.entity.getNavigation().isFollowingPath()) return;

        int lr = (int) (Math.random() * 2);
        int distance_i = (int) (Math.random() * 2) + 2;
        int distance_k = (int) (Math.random() * 2) + 2;

        BlockPos strafeLocation = this.entity.getBlockPos().add(lr == 0 ? distance_i : -distance_i, 0, lr == 1 ? distance_k : -distance_k);
        this.entity.getNavigation().startMovingTo(strafeLocation.getX(), strafeLocation.getY(), strafeLocation.getZ(), this.speed * 1.25f);

        Main.LOGGER.info(String.format("DodgeArrowGoal: %s trying to dodge an arrow!", this.entity.getName().getString()));
    }

    private boolean targetShootingBow() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) return false;

        ItemStack mainHandStack = target.getMainHandStack();
        ItemStack offHandStack = target.getOffHandStack();

        if (!mainHandStack.getItem().equals(Items.BOW) && !offHandStack.getItem().equals(Items.BOW)) return false;
        return target.isUsingItem();
    }
}
