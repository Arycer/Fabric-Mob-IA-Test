package me.arycer.eufoniatestai.client;

import me.arycer.eufoniatestai.client.entity.BirdRenderer;
import me.arycer.eufoniatestai.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ClientMain implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.BIRD, BirdRenderer::new);
    }
}