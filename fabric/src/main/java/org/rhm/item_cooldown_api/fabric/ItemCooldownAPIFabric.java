package org.rhm.item_cooldown_api.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.rhm.item_cooldown_api.ItemCooldownAPIBase;
import org.rhm.item_cooldown_api.ItemCooldownAPICommon;

import java.nio.file.Path;
import java.util.function.Supplier;


//? if >=1.20.5
import net.minecraft.core.component.DataComponentType;

@ApiStatus.Internal
public class ItemCooldownAPIFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		ItemCooldownAPICommon.init(new Impl());
		System.out.println(ItemCooldownAPICommon.getImplementation().getConfigPath());

		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			if (server.isDedicatedServer())
				ItemCooldownAPICommon.tick();
		});
		ClientTickEvents.END_CLIENT_TICK.register((client) -> ItemCooldownAPICommon.tick());
		ServerLifecycleEvents.SERVER_STARTED.register(ItemCooldownAPICommon::afterInit);
	}

	public static final class Impl implements ItemCooldownAPIBase {
		//? if >=1.20.5 {
		@Override
		public <T> Supplier<DataComponentType<T>> registerDataComponentType(String id, DataComponentType<T> type) {
			DataComponentType<T> registered = Registry.register(
					BuiltInRegistries.DATA_COMPONENT_TYPE,
					ItemCooldownAPICommon.id(id),
					type
			);
			return () -> registered;
		}
		//?}

		@Override
		public Path getConfigPath() {
			return FabricLoader.getInstance().getConfigDir();
		}
	}
}
