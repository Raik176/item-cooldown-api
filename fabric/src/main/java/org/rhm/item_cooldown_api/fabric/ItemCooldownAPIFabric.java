package org.rhm.item_cooldown_api.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.rhm.item_cooldown_api.ItemCooldownAPIBase;
import org.rhm.item_cooldown_api.ItemCooldownAPICommon;

public class ItemCooldownAPIFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		ItemCooldownAPICommon.init(new ItemCooldownAPIBase() {
			@Override
			public <T> MappedRegistry<T> registerRegistry(ResourceKey<Registry<T>> key) {
				return FabricRegistryBuilder.<T>createSimple(key)
						.attribute(RegistryAttribute.SYNCED)
						.buildAndRegister();
			}
		});
	}
}
