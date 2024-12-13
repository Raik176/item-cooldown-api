package org.rhm.item_cooldown_api.forge;

import net.minecraft.core.registries.Registries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import org.rhm.item_cooldown_api.ItemCooldownAPIBase;
import org.rhm.item_cooldown_api.ItemCooldownAPICommon;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;
import java.util.function.Supplier;


//? if >=1.20.5
import net.minecraft.core.component.DataComponentType;

@Mod(ItemCooldownAPICommon.MOD_ID)
public class ItemCooldownAPIForge {
	public ItemCooldownAPIForge(FMLJavaModLoadingContext context) {
		IEventBus modBus = context.getModEventBus();
		ItemCooldownAPICommon.init(new Impl());
		Impl.register(modBus);

		MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);
		MinecraftForge.EVENT_BUS.addListener(this::onServerTick);
		MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
	}

	public void onServerStarted(ServerStartedEvent event) {
		ItemCooldownAPICommon.afterInit(event.getServer());
	}

	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.side == LogicalSide.SERVER)
			ItemCooldownAPICommon.tick();
	}

	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.side == LogicalSide.CLIENT) // just making sure
			ItemCooldownAPICommon.tick();
	}

	public static final class Impl implements ItemCooldownAPIBase {
		//? if >=1.20.5 {
		private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_REGISTRY = DeferredRegister.create(
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
