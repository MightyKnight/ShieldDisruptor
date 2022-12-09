package net.heyzeer0.sd.configs;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Config(name = "shieldisruptor_general")
public class GeneralConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    String modVersion = "";

    public boolean isEnabled = true;

    public List<String> hiddenItems = Arrays.asList(
            Registries.ITEM.getId(Items.SHIELD).toString(),
            Registries.ITEM.getId(Items.TOTEM_OF_UNDYING).toString(),
            "#c:shields");

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
        if(modVersion == "") {
            if(!hiddenItems.contains("#c:shields")) {
                hiddenItems.add("#c:shields");
            }
            modVersion = "1.5.0";
        }

        // Remove empty list items and trim strings
        hiddenItems.removeIf(String::isBlank);
        hiddenItems.replaceAll(String::trim);

        ConfigData.super.validatePostLoad();
    }
}
