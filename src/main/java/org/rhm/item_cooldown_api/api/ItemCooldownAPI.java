package org.rhm.item_cooldown_api.api;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.rhm.item_cooldown_api.ItemCooldownAPICommon;
import org.rhm.item_cooldown_api.component.ItemCooldownComponents;

public class ItemCooldownAPI {
    private static final ResourceKey<Registry<Cooldown>> COOLDOWN_REGISTRY_KEY = ItemCooldownAPICommon.registryKey("cooldown");
    public static final MappedRegistry<Cooldown> COOLDOWN_REGISTRY = ItemCooldownAPICommon.getImplementation()
            .registerRegistry(COOLDOWN_REGISTRY_KEY);

    public static Holder.Reference<Cooldown> register(ResourceLocation id, Cooldown cooldown) {
        return COOLDOWN_REGISTRY.register(
                ResourceKey.create(COOLDOWN_REGISTRY_KEY, id),
                cooldown,
                RegistrationInfo.BUILT_IN
        );
    }

    @Nullable
    public static ItemCooldownComponents.CooldownComponent getItemCooldown(ItemStack stack) {
        return null;
    }

    @Nullable
    public static ItemCooldownComponents.StoredCooldownComponent getItemStoredCooldowns(ItemStack stack) {
        return null;
    }
}
