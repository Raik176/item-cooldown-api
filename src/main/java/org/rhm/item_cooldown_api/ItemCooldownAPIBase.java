package org.rhm.item_cooldown_api;


import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;

public interface ItemCooldownAPIBase {
    <T> MappedRegistry<T> registerRegistry(ResourceKey<Registry<T>> key);
    <T>DataComponentType<T> registerDataComponentType(String id, DataComponentType<T> type);
}
