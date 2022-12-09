package net.heyzeer0.sd.configs;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.List;

@Config(name = "shieldisruptor_general")
public class GeneralConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    String modVersion = "";

    public boolean isEnabled = true;

    @ConfigEntry.Gui.Tooltip
    public boolean hideShields = true;

    public boolean hideInMainHand = false;

    @ConfigEntry.Gui.Tooltip
    public List<String> hiddenItems = Arrays.asList(
            Registry.ITEM.getId(Items.TOTEM_OF_UNDYING).toString());

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
        hiddenItems.removeIf(String::isBlank);
        hiddenItems.replaceAll(String::trim);

        ConfigData.super.validatePostLoad();
    }
}
