package org.rhm.item_cooldown_api.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.rhm.item_cooldown_api.api.ItemCooldownAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @WrapOperation(
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemCooldowns;getCooldownPercent(Lnet/minecraft/world/item/Item;F)F"
            )
    )
    private float renderItemDecorations(ItemCooldowns instance, Item cooldownInstance, float g,
                                        Operation<Float> original, Font font, ItemStack itemStack, int i, int j,
                                        @Nullable String string) {
        float f = ItemCooldownAPI.getCooldownProgress(itemStack, Objects.requireNonNull(Minecraft.getInstance().player).getUUID());
        return f == 0 ? original.call(instance, cooldownInstance, g) : f;
    }
}
