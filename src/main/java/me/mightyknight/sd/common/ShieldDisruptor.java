package me.mightyknight.sd.common;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class ShieldDisruptor implements ModInitializer {

    private static ShieldDisruptor main;

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
