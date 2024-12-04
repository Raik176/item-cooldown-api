package org.rhm.item_cooldown_api.forge;

import org.rhm.item_cooldown_api.ItemCooldownAPICommon;
import net.minecraftforge.fml.common.Mod;

@Mod(ItemCooldownAPICommon.MOD_ID)
public class ItemCooldownAPIForge {
	public ItemCooldownAPIForge() {
		ItemCooldownAPICommon.init();
	}
}
