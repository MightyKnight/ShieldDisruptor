package me.mightyknight.sd.mcpre120.mixin;

import com.github.crimsondawn45.fabricshieldlib.lib.object.FabricShield;
import me.mightyknight.sd.common.SDConfig;
import me.mightyknight.sd.common.ShieldDisruptor;
import me.mightyknight.sd.multiversion_mixin.VersionedMixin;
import me.mightyknight.sd.versioned.Versioned;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
@VersionedMixin("<=1.19.3")
public class MixinHeldItemRendererPre120 {

    @Inject(at =
    @At(value = "HEAD"),
            method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            cancellable = true
    )

    private void hideShield(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo callback) {

        if (!ShieldDisruptor.getMain().getConfig().isEnabled || entity != MinecraftClient.getInstance().player) return;
        if (!MinecraftClient.getInstance().options.getPerspective().isFirstPerson() || stack.isEmpty() || entity.isUsingItem()) return;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
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

            // Block items in the item tag "c:shields"
            if(Versioned.REGISTRY.stackHasTag(stack, new Identifier("c", "shields"))) {
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
            if (!tagKey.matches("#[a-z0-9_.-]+:[a-z0-9_.-]+")) {
                continue;
            }

            // Check if item has the tag
            Identifier tagId = new Identifier(
                    tagKey.split(":")[0].replaceFirst("#", ""),
                    tagKey.split(":")[1]);

            if(Versioned.REGISTRY.stackHasTag(stack, tagId)) {
                callback.cancel();
                return;
            }

        }


    }

}

