package net.heyzeer0.sd.mixins;

import net.heyzeer0.sd.ModCore;
import net.heyzeer0.sd.configs.GeneralConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

    @Inject(at =
        @At(value = "HEAD"),
        method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        cancellable = true
    )
    private void hideShield(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo callback) {
        if (!ModCore.getMain().getGeneralConfig().isEnabled || entity != MinecraftClient.getInstance().player) return;
        if (!MinecraftClient.getInstance().options.getPerspective().isFirstPerson() || stack.isEmpty() || entity.isUsingItem()) return;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.getOffHandStack() != stack) return;

        GeneralConfig config = ModCore.getMain().getGeneralConfig();
        String id = Registries.ITEM.getId(stack.getItem()).toString();

        // Check if the item is specified in config
        if(config.contains(id) || (id.startsWith("minecraft:") && config.contains(id.substring(10)))) {
            callback.cancel();
        }

        // Check if the item has a tag that was specified in config
        for(String tagKey : ModCore.getMain().getGeneralConfig().hiddenItems) {

            // Validate tag to prevent crashes
            if(!tagKey.matches("#[a-z0-9_.-]+:[a-z0-9_.-]+")) {
                continue;
            }

            TagKey t = TagKey.of(
                    RegistryKeys.ITEM,
                    new Identifier(
                            tagKey.split(":")[0].replaceFirst("#", ""),
                            tagKey.split(":")[1]));
            if(stack.isIn(t)) {
                callback.cancel();
            }
        }
    }

}
