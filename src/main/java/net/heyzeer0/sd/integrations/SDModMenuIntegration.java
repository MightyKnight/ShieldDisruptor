package net.heyzeer0.sd.integrations;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.heyzeer0.sd.configs.GeneralConfig;

public class SDModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> AutoConfig.getConfigScreen(GeneralConfig.class, screen).get();
    }

}
