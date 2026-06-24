package me.mightyknight.sd.versioned;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;

public interface RegistryHandler {

    String getItemId(Item id);

    boolean stackHasTag(ItemStack stack, Identifier tagId);

}
