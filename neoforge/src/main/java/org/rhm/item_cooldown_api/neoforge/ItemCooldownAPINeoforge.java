package org.rhm.item_cooldown_api.neoforge;

import org.rhm.item_cooldown_api.ItemCooldownAPICommon;
import net.neoforged.fml.common.Mod;

@Mod(ItemCooldownAPICommon.MOD_ID)
public class ItemCooldownAPINeoforge {
	public ItemCooldownAPINeoforge() {
		ItemCooldownAPICommon.init();
	}
}
