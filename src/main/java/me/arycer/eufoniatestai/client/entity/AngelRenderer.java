package me.arycer.eufoniatestai.client.entity;

import me.arycer.eufoniatestai.Main;
import me.arycer.eufoniatestai.entity.custom.AngelEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AngelRenderer extends GeoEntityRenderer<AngelEntity> {
    public AngelRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AngelModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public Identifier getTextureLocation(AngelEntity entity) {
        return new Identifier(Main.MOD_ID, "textures/entity/angel.png");
    }

    @Override
    public void render(AngelEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferIn, int packedLightIn) {
        if (entity.isBaby()) poseStack.scale(0.5f, 0.5f, 0.5f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferIn, packedLightIn);
    }
}
