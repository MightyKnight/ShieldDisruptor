package me.mightyknight.sd.versioned;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public interface RegistryHandler {

    String getItemId(Item id);

    boolean stackHasTag(ItemStack stack, Identifier tagId);

}
