package me.mightyknight.sd.mc20.impl;

import me.mightyknight.sd.versioned.RegistryHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class RegistryHandlerImpl implements RegistryHandler {

    @Override
    public String getItemId(Item item) {
        return Registries.ITEM.getId(item).toString();
    }

    @Override
    public boolean stackHasTag(ItemStack stack, Identifier tagId) {
        TagKey<Item> tag = TagKey.of(RegistryKeys.ITEM, tagId);
        if(tag != null) {
            return stack.isIn(tag);
        }
        return false;
    }

}
