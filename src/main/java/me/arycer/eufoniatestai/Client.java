package me.arycer.eufoniatestai;

import me.arycer.eufoniatestai.entity.ModEntities;
import me.arycer.eufoniatestai.entity.client.TigerRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.TIGER, TigerRenderer::new);

        System.out.println("Client iniciado!");
    }
}
