package me.arycer.eufoniatestai;

import me.arycer.eufoniatestai.entity.ModEntities;
import me.arycer.eufoniatestai.entity.custom.TigerEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;

public class Main implements ModInitializer {
    public static final String MOD_ID = "eufonia-test-ai";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        GeckoLib.initialize();

        FabricDefaultAttributeRegistry.register(ModEntities.TIGER, TigerEntity.setAttributes());

        LOGGER.info("Mod iniciado!");
    }
}
