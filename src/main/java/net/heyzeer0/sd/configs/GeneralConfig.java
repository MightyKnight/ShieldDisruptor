package net.heyzeer0.sd.configs;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.List;

@Config(name = "shieldisruptor_general")
public class GeneralConfig implements ConfigData {

    public boolean isEnabled = true;

    public List<String> hiddenItems = Arrays.asList(
            Registry.ITEM.getId(Items.SHIELD).toString(),
            Registry.ITEM.getId(Items.TOTEM_OF_UNDYING).toString());

}
