package me.mightyknight.sd.mc16.impl;

import me.mightyknight.sd.versioned.RegistryHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryHandlerImpl implements RegistryHandler {

    @Override
    public String getItemId(Item item) {
        return Registry.ITEM.getId(item).toString();
    }

    @Override
    public boolean stackHasTag(ItemStack stack, Identifier tagId) {
        Tag<Item> tag  = ItemTags.getTagGroup().getTag(tagId);
        if(tag != null) {
            return stack.getItem().isIn(tag);
        }
        return false;
    }

}
