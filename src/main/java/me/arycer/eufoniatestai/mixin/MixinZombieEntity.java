package me.arycer.eufoniatestai.mixin;

import me.arycer.eufoniatestai.goal.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ZombieEntity.class)
public class MixinZombieEntity extends HostileEntity {
    public MixinZombieEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
        this.getNavigation().setCanSwim(true);
        this.setCanPickUpLoot(true);
    }

    @Override
    public void initGoals() {
        this.goalSelector.add(1, new FollowConcreteGoal(this));

        /*
        this.goalSelector.add(2, new ZombieAttackGoal((ZombieEntity) (Object) this, 1.0, false));

        this.goalSelector.add(6, new MoveThroughVillageGoal(this, 1.0, true, 4, () -> true));
        //this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));

        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, false));

        initCustomAI();
        */
    }

    private void initCustomAI() {
        this.goalSelector.add(1, new DodgeProjectileGoal(this, 1.2D));

        this.goalSelector.add(3, new BreakBlockGoal(this));
        this.goalSelector.add(3, new TowerUpGoal(this));
        this.goalSelector.add(3, new CutWoodGoal(this));
    }

    @Override
    protected boolean prefersNewEquipment(ItemStack newStack, ItemStack oldStack) {
        if (!newStack.getItem().equals(oldStack.getItem())) return super.prefersNewEquipment(newStack, oldStack);
        if (!(newStack.getItem() instanceof BlockItem)) return super.prefersNewEquipment(newStack, oldStack);

        int count = newStack.getCount() + oldStack.getCount();
        if (count > newStack.getMaxCount()) {
            newStack.setCount(newStack.getMaxCount());
            oldStack.setCount(count - newStack.getMaxCount());
        } else {
            newStack.setCount(count);
            oldStack.setCount(0);
        }
        return true;
    }
}
