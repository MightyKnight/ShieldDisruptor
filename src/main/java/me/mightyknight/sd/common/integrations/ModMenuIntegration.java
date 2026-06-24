package me.mightyknight.sd.common.integrations;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.mightyknight.sd.common.SDConfig;
import me.shedaniel.autoconfig.AutoConfigClient;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> AutoConfigClient.getConfigScreen(SDConfig.class, screen).get();
    }

}
