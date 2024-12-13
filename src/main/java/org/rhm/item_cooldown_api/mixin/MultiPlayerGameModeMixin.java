package org.rhm.item_cooldown_api.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.rhm.item_cooldown_api.api.ItemCooldownAPI;
import org.rhm.item_cooldown_api.component.ItemCooldownComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @WrapOperation(
            method = "performUseItemOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemCooldowns;isOnCooldown(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private boolean performUseItemOn(ItemCooldowns instance, Item arg, Operation<Boolean> original,
                              LocalPlayer localPlayer, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return ItemCooldownAPI.isItemOnCooldown(
                localPlayer.getItemInHand(interactionHand),
                Minecraft.getInstance().player.getUUID()
        ) || original.call(instance, arg);
    }

    //extremely hacky workaround
    @Unique
    private Player item_cooldown_api$player;
    @Unique
    private InteractionHand item_cooldown_api$hand;

    @Inject(
            method = "useItem",
            at = @At("HEAD")
    )
    private void useItem(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        this.item_cooldown_api$player = player;
        this.item_cooldown_api$hand = interactionHand;
    }

    @WrapOperation(
            method = "method_41929",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemCooldowns;isOnCooldown(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private boolean useItem(ItemCooldowns instance, Item arg, Operation<Boolean> original) {
        return ItemCooldownAPI.isItemOnCooldown(
                item_cooldown_api$player.getItemInHand(item_cooldown_api$hand),
                item_cooldown_api$player.getUUID()
        ) || original.call(instance, arg);
    }
}
