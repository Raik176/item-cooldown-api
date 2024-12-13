package org.rhm.item_cooldown_api.neoforge;


import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.rhm.item_cooldown_api.ItemCooldownAPIBase;
import org.rhm.item_cooldown_api.ItemCooldownAPICommon;
import net.neoforged.fml.common.Mod;

import java.nio.file.Path;
import java.util.function.Supplier;


//? if >=1.20.5
import net.minecraft.core.component.DataComponentType;

@Mod(ItemCooldownAPICommon.MOD_ID)
public class ItemCooldownAPINeoforge {
	public ItemCooldownAPINeoforge(IEventBus eventBus, ModContainer container) {
		ItemCooldownAPICommon.init(new Impl());
		Impl.register(eventBus);
	}

	public static final class Impl implements ItemCooldownAPIBase {
		//? if >=1.20.5 {
		private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_REGISTRY = DeferredRegister.createDataComponents(
				//? if >=1.21.1
				Registries.DATA_COMPONENT_TYPE,
				ItemCooldownAPICommon.MOD_ID
		);

		@Override
		public <T> Supplier<DataComponentType<T>> registerDataComponentType(String id, DataComponentType<T> type) {
			return DATA_COMPONENT_REGISTRY.register(id, () -> type);
		}
		//?}

		@Override
		public Path getConfigPath() {
			return FMLPaths.CONFIGDIR.get();
		}

		public static void register(IEventBus eventBus) {
			//? if >=1.20.5
			DATA_COMPONENT_REGISTRY.register(eventBus);
		}
	}
}
