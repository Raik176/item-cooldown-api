package org.rhm.item_cooldown_api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.rhm.item_cooldown_api.api.Cooldown;
import org.rhm.item_cooldown_api.api.ItemCooldownAPI;
import org.rhm.item_cooldown_api.component.ItemCooldownComponents;

public class ItemCooldownAPICommon {
	public static final String MOD_ID = "item_cooldown_api";
	private static ItemCooldownAPIBase impl = null;


	private static boolean isInitialized = false;
	public static void init(ItemCooldownAPIBase implementation) {
		if (isInitialized) return;
		impl = implementation;

		ItemCooldownComponents.init();

		isInitialized = true;

		ItemCooldownAPI.register(id("test"), new Cooldown.Builder().build());
	}

	public static ItemCooldownAPIBase getImplementation() {
		return impl;
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.tryBuild(MOD_ID, path);
	}

	public static <T> ResourceKey<Registry<T>> registryKey(String path) {
		return ResourceKey.createRegistryKey(id(path));
	}
}
