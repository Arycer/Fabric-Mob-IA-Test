package me.arycer.eufoniatestai.client;

import me.arycer.eufoniatestai.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;

public class ClientMain implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModEntities.registerRenderers();
    }
}