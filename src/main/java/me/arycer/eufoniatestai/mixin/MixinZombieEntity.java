package me.arycer.eufoniatestai.mixin;

import me.arycer.eufoniatestai.goal.BreakBlockGoal;
import me.arycer.eufoniatestai.goal.DodgeArrowGoal;
import me.arycer.eufoniatestai.goal.TowerUpGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ZombieEntity.class)
public class MixinZombieEntity extends HostileEntity {
    public MixinZombieEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void initGoals() {
        this.goalSelector.add(1, new BreakBlockGoal(this));
        this.goalSelector.add(1, new DodgeArrowGoal(this, 1.2D));
        this.goalSelector.add(1, new TowerUpGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2D, false));

        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
    }
}
