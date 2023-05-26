package me.arycer.eufoniatestai;

import me.arycer.eufoniatestai.poi.PointsOfInterest;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Arycer's Test AI for Eufonia Studio");
    public static final String MOD_ID = "test-ai";

    @Override
    public void onInitialize() {
        PointsOfInterest.register();

        LOGGER.info("Mod iniciado!");
    }
}
