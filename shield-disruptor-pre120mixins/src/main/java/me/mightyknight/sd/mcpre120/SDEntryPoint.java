package me.mightyknight.sd.mcpre120;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SDEntryPoint implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        SDEntryPoint.LOGGER.debug("Loading mixins for version pre1.20");
    }

}
