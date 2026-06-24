package me.mightyknight.sd.common.mixin;

import com.github.crimsondawn45.fabricshieldlib.lib.object.FabricShield;
import me.mightyknight.sd.common.SDConfig;
import me.mightyknight.sd.common.ShieldDisruptor;
import me.mightyknight.sd.multiversion_mixin.ReflectionUtils;
import me.mightyknight.sd.multiversion_mixin.VersionedMixin;
import me.mightyknight.sd.versioned.Versioned;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
@VersionedMixin(min="1.21.9")
public class MixinItemInHandRenderer {

    @Inject(at =
    @At(value = "HEAD"),
            method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
            cancellable = true
    )
    private void hideShield(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, PoseStack matrices, SubmitNodeCollector queue, int light, CallbackInfo callback) {

        if (!ShieldDisruptor.getMain().getConfig().isEnabled || entity != Minecraft.getInstance().player) return;
        if (!Minecraft.getInstance().options.getPerspective().isFirstPerson() || stack.isEmpty() || entity.isUsingItem()) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) return;

        SDConfig config = ShieldDisruptor.getMain().getConfig();

        // Hide offhand only or both based on config option
        // E.g. if main hand is disabled, check if the item is the same as in offhand and otherwise quit
        if(!config.hideInMainHand) {
            if(player.getOffHandStack() != stack) return;
        }

        // Hide all shields that are a "ShieldItem" or in the tag "c:shields" for maximum compatibility
        if(config.hideShields) {

            // Block the normal minecraft shield and all shields extending from it
            if(stack.getItem() instanceof ShieldItem) {
                callback.cancel();
                return;
            }

            // Block shields from FabricShieldLib if it is loaded
            if(FabricLoader.getInstance().isModLoaded("fabricshieldlib") && stack.getItem() instanceof FabricShield) {
                callback.cancel();
                return;
            }

            // Block items in the tag "c:tools/shields" (>1.20.5)
            if(Versioned.REGISTRY.stackHasTag(stack, ReflectionUtils.constructIdentifier("c", "tools/shields"))) {
                callback.cancel();
                return;
            }

            // Block items in the item tag "c:shields"
            // [LEGACY] This tag was changed to "c:tools/shields" in 1.20.5
            if(Versioned.REGISTRY.stackHasTag(stack, ReflectionUtils.constructIdentifier("c", "shields"))) {
                callback.cancel();
                return;
            }

        }

        // Check if the item is specified in config
        String id = Versioned.REGISTRY.getItemId(stack.getItem());
        if(config.contains(id) || (id.startsWith("minecraft:") && config.contains(id.substring(10)))) {
            callback.cancel();
            return;
        }

        // Check if the item has a tag that was specified in config
        for(String tagKey : ShieldDisruptor.getMain().getConfig().hiddenItems) {

            // Validate tag to prevent crashes
            if (!tagKey.matches("#[a-z0-9_.-]+:[a-z0-9_.-/]+")) {
                continue;
            }

            // Check if item has the tag
            Identifier tagId = ReflectionUtils.constructIdentifier(
                    tagKey.split(":")[0].replaceFirst("#", ""),
                    tagKey.split(":")[1]);

            if(Versioned.REGISTRY.stackHasTag(stack, tagId)) {
                callback.cancel();
                return;
            }

        }


    }

}
