package me.mightyknight.sd.mc18.impl;

import me.mightyknight.sd.versioned.RegistryHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryHandlerImpl implements RegistryHandler {

    @Override
    public String getItemId(Item item) {
        return Registry.ITEM.getId(item).toString();
    }

    @Override
    public boolean stackHasTag(ItemStack stack, Identifier tagId) {
        TagKey<Item> tag = TagKey.of(Registry.ITEM_KEY, tagId);
        if(tag != null) {
            return stack.isIn(tag);
        }
        return false;
    }

}
