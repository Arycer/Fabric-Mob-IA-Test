package me.arycer.eufoniatestai.client.entity;

import me.arycer.eufoniatestai.Main;
import me.arycer.eufoniatestai.entity.custom.AngelEntity;
import me.arycer.eufoniatestai.entity.custom.BirdEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class AngelModel extends GeoModel<AngelEntity> {
    @Override
    public Identifier getModelResource(AngelEntity animatable) {
        return new Identifier(Main.MOD_ID, "geo/angel.geo.json");
    }

    @Override
    public Identifier getTextureResource(AngelEntity animatable) {
        return new Identifier(Main.MOD_ID, "textures/entity/angel.png");
    }

    @Override
    public Identifier getAnimationResource(AngelEntity animatable) {
        return new Identifier(Main.MOD_ID, "animations/angel.animation.json");
    }

    @Override
    public void setCustomAnimations(AngelEntity animatable, long instanceId, AnimationState<AngelEntity> animationState) {
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");

        if (head == null) return;

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        if (entityData == null) return;

        head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
        head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
    }
}
