package org.rhm.item_cooldown_api.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.rhm.item_cooldown_api.api.ItemCooldownAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @WrapOperation(
            method = "useItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemCooldowns;isOnCooldown(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private boolean useItemOn(ItemCooldowns instance, Item arg, Operation<Boolean> original,
                              ServerPlayer serverPlayer, Level level, ItemStack itemStack,
                              InteractionHand interactionHand) {
        return ItemCooldownAPI.isItemOnCooldown(
                itemStack,
                serverPlayer.getUUID()
        ) || original.call(instance, arg);
    }

    @WrapOperation(
            method = "useItemOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemCooldowns;isOnCooldown(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private boolean useItemOn(ItemCooldowns instance, Item arg, Operation<Boolean> original,
                              ServerPlayer serverPlayer, Level level, ItemStack itemStack,
                              InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return ItemCooldownAPI.isItemOnCooldown(
                itemStack,
                serverPlayer.getUUID()
        ) || original.call(instance, arg);
    }
}