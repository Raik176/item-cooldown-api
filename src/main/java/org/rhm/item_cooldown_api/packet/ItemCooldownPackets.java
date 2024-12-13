package org.rhm.item_cooldown_api.packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import commonnetwork.api.Network;
import net.minecraft.Util;
import org.rhm.item_cooldown_api.api.ItemCooldownAPI;

public class ItemCooldownPackets {
    public static final BiMap<Integer, Class<? extends ItemCooldownAPI.CooldownInfo>> COOLDOWN_ID_MAPPING = Util.make(HashBiMap.create(), (map) -> {
        map.put(0, ItemCooldownAPI.IndividualCooldownInfo.class);
        map.put(1, ItemCooldownAPI.SharedCooldownInfo.class);
    });

    @SuppressWarnings("deprecation")
    public static void init() {
        Network.registerPacket(
                RequestItemCooldownInfoPacket.CHANNEL,
                RequestItemCooldownInfoPacket.class,
                RequestItemCooldownInfoPacket::encode,
                RequestItemCooldownInfoPacket::new,
                RequestItemCooldownInfoPacket::handle
        ).registerPacket(
                ItemCooldownInfoResponsePacket.CHANNEL,
                ItemCooldownInfoResponsePacket.class,
                ItemCooldownInfoResponsePacket::encode,
                ItemCooldownInfoResponsePacket::new,
                ItemCooldownInfoResponsePacket::handle
        );;
    }
}
