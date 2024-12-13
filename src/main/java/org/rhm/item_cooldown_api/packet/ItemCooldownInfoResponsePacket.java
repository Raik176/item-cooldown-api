package org.rhm.item_cooldown_api.packet;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.rhm.item_cooldown_api.ItemCooldownAPICommon;
import org.rhm.item_cooldown_api.ItemCooldownManager;
import org.rhm.item_cooldown_api.api.ItemCooldownAPI;

public class ItemCooldownInfoResponsePacket {
    public static final ResourceLocation CHANNEL = ItemCooldownAPICommon.id("item_cooldown_info_response");
    private final boolean invalid;
    private final int cooldownInfoId;
    private final ItemCooldownAPI.CooldownInfo info;

    public ItemCooldownInfoResponsePacket(boolean invalid, int cooldownInfoId, ItemCooldownAPI.CooldownInfo info) {
        this.invalid = invalid;
        this.cooldownInfoId = cooldownInfoId;

        this.info = info;
        if (!ItemCooldownManager.CooldownId.fromId(cooldownInfoId).isValid(info)) {
            ItemCooldownAPICommon.logger.warn("Invalid cooldown info. Id: {}, Info: {}", cooldownInfoId, info);
        }
    }

    public ItemCooldownInfoResponsePacket(FriendlyByteBuf friendlyByteBuf) {
        this.invalid = friendlyByteBuf.readBoolean();
        this.cooldownInfoId = friendlyByteBuf.readInt();

        this.info = ItemCooldownManager.CooldownId.fromId(cooldownInfoId).decode(friendlyByteBuf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(invalid);
        buf.writeInt(cooldownInfoId);
        if (!invalid)
            info.encode(buf);
    }

    public static void handle(PacketContext<ItemCooldownInfoResponsePacket> ctx) {
        if (ctx.side().equals(Side.CLIENT)) {
            ItemCooldownInfoResponsePacket packet = ctx.message();
            if (!packet.invalid) {
                ItemCooldownAPICommon.manager.addCooldown(packet.info);
            } else {
                ItemCooldownAPICommon.logger.warn("Received invalid {}. Id: {}, Info: {}",
                        ItemCooldownInfoResponsePacket.class.getSimpleName(), packet.cooldownInfoId, packet.info);
            }
        }
    }
}
