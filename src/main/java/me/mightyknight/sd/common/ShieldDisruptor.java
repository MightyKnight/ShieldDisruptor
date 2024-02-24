package me.mightyknight.sd.common;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShieldDisruptor implements ModInitializer {

    private static ShieldDisruptor main;
    public static final Logger LOGGER = LogManager.getLogger();

    ConfigHolder<SDConfig> config;

    @Override
    public void onInitialize() {
        main = this;
        config = AutoConfig.register(SDConfig.class, GsonConfigSerializer::new);
    }

    public SDConfig getConfig() {
        return config.getConfig();
    }

    public static ShieldDisruptor getMain() {
        return main;
    }

}
