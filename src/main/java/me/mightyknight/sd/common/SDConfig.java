package me.mightyknight.sd.common;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Config(name = "shieldisruptor_general")
public class SDConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    String modVersion = "";

    public boolean isEnabled = true;

    @ConfigEntry.Gui.Tooltip
    public boolean hideShields = true;

    public boolean hideInMainHand = false;

    @ConfigEntry.Gui.Tooltip
    public List<String> hiddenItems = new ArrayList<>(Collections.singletonList(
            "minecraft:totem_of_undying"));

    // Ignore case and leading/trailing whitespaces when checking if entry is in config
    public boolean contains(String s) {
        for(String item : hiddenItems) {
            if(s.equalsIgnoreCase(item.trim())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void validatePostLoad() throws ValidationException {

        // Upgrade from <=1.4.0, adding new tag "#c:shields"
        if(modVersion.equals("")) {
            if(!hiddenItems.contains("#c:shields")) {
                hiddenItems.add("#c:shields");
            }
            modVersion = "1.5.0";
        }

        // Update from 1.5.0, remove #c:shields and shield,
        // since this is now regulated by the extra button "Hide ALL shields"
        if (modVersion.equals("1.5.0")) {
            hiddenItems.remove("minecraft:shield");
            hiddenItems.remove("#c:shields");
            modVersion = "1.5.1";
        }

        // Remove empty list items and trim strings
        hiddenItems.removeIf(StringUtils::isBlank);
        hiddenItems.replaceAll(String::trim);

        ConfigData.super.validatePostLoad();
    }
}

