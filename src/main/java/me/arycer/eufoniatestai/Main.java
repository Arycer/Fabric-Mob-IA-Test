package me.arycer.eufoniatestai;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;

public class Main implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Arycer's Test AI for Eufonia Studio");

    @Override
    public void onInitialize() {
        GeckoLib.initialize();

        LOGGER.info("Mod iniciado!");
    }
}
