package me.arycer.eufoniatestai.client.entity;

import me.arycer.eufoniatestai.Main;
import me.arycer.eufoniatestai.entity.custom.BirdEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BirdRenderer extends GeoEntityRenderer<BirdEntity> {
    public BirdRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BirdModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public Identifier getTextureLocation(BirdEntity entity) {
        return new Identifier(Main.MOD_ID, "textures/entity/bird.png");
    }

    @Override
    public void render(BirdEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferIn, int packedLightIn) {
        if (entity.isBaby()) poseStack.scale(0.5f, 0.5f, 0.5f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferIn, packedLightIn);
    }
}
