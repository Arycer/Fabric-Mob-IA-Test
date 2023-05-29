package me.arycer.eufoniatestai.client.entity;

import me.arycer.eufoniatestai.Main;
import me.arycer.eufoniatestai.entity.custom.BirdEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BirdModel extends GeoModel<BirdEntity> {
    @Override
    public Identifier getModelResource(BirdEntity animatable) {
        return new Identifier(Main.MOD_ID, "geo/bird.geo.json");
    }

    @Override
    public Identifier getTextureResource(BirdEntity animatable) {
        return new Identifier(Main.MOD_ID, "textures/entity/bird.png");
    }

    @Override
    public Identifier getAnimationResource(BirdEntity animatable) {
        return new Identifier(Main.MOD_ID, "animations/bird.animation.json");
    }

    @Override
    public void setCustomAnimations(BirdEntity animatable, long instanceId, AnimationState<BirdEntity> animationState) {
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");

        if (head == null) return;

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        if (entityData == null) return;

        head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
        head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
    }
}
