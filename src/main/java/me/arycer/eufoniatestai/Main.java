package me.arycer.eufoniatestai;

import me.arycer.eufoniatestai.entity.ModEntities;
import me.arycer.eufoniatestai.entity.custom.BirdEntity;
import me.arycer.eufoniatestai.poi.PointsOfInterest;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;

public class Main implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Arycer's Test AI for Eufonia Studio");
    public static final String MOD_ID = "test-ai";

    @Override
    public void onInitialize() {
        GeckoLib.initialize();
        
        PointsOfInterest.register();
        ModEntities.registerEntities();

        LOGGER.info("Mod iniciado!");
    }
}
