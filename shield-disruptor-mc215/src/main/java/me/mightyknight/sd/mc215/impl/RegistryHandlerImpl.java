package me.mightyknight.sd.mc215.impl;

import me.mightyknight.sd.versioned.RegistryHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.Identifier;

public class RegistryHandlerImpl implements RegistryHandler {

    @Override
    public String getItemId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    @Override
    public boolean stackHasTag(ItemStack stack, Identifier tagId) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, tagId);
        if(tag != null) {
            return stack.is(tag);
        }
        return false;
    }

}
