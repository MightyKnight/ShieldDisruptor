package net.heyzeer0.sd;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.heyzeer0.sd.configs.GeneralConfig;

public class ModCore implements ModInitializer {

    private static ModCore main;

    ConfigHolder<GeneralConfig> generalConfig;

    @Override
    public void onInitialize() {
        main = this;

        generalConfig = AutoConfig.register(GeneralConfig.class, GsonConfigSerializer::new);
    }

    public GeneralConfig getGeneralConfig() {
        return generalConfig.getConfig();
    }

    public static ModCore getMain() {
        return main;
    }

}
