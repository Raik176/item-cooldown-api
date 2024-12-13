package org.rhm.item_cooldown_api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.rhm.item_cooldown_api.component.ItemCooldownComponents;

//? if <1.16 {
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
*///?} else {
import org.rhm.item_cooldown_api.packet.ItemCooldownPackets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//?}

@ApiStatus.Internal
public class ItemCooldownAPICommon {
	public static final String MOD_ID = "item_cooldown_api";
	private static ItemCooldownAPIBase impl = null;
	public static ItemCooldownManager manager = null;
	//? if <1.16 {
	/*public static final Logger logger = LogManager.getLogger("Mod Credits Reborn");
	 *///?} else
	public static final Logger logger = LoggerFactory.getLogger("Item Cooldown API");


	private static boolean isInitialized = false;
	public static void init(ItemCooldownAPIBase implementation) {
		if (isInitialized) return;
		impl = implementation;
		manager = new ItemCooldownManager();

		ItemCooldownPackets.init();
		ItemCooldownComponents.init();

		isInitialized = true;
	}

	public static void afterInit(MinecraftServer server) {
		manager.load(server);
	}

	public static void onSave(MinecraftServer server) {
		manager.save(server);
	}

	public static void tick() {
		manager.tick();
	}

	public static ItemCooldownAPIBase getImplementation() {
		return impl;
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.tryBuild(MOD_ID, path);
	}
}
