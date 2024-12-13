package org.rhm.item_cooldown_api.packet;

import commonnetwork.api.Dispatcher;
import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.rhm.item_cooldown_api.ItemCooldownAPICommon;
import org.rhm.item_cooldown_api.api.ItemCooldownAPI;

import java.util.UUID;

public class RequestItemCooldownInfoPacket {
    public static final ResourceLocation CHANNEL = ItemCooldownAPICommon.id("request_item_cooldown_info");
    private final UUID uuid;

    public RequestItemCooldownInfoPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public RequestItemCooldownInfoPacket(FriendlyByteBuf friendlyByteBuf) {
        this.uuid = friendlyByteBuf.readUUID();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
    }

    public static void handle(PacketContext<RequestItemCooldownInfoPacket> ctx) {
        if (ctx.side().equals(Side.SERVER)) {
            if (ItemCooldownAPICommon.manager.cooldownExists(ctx.message().uuid)) {
                ItemCooldownAPI.CooldownInfo info = ItemCooldownAPICommon.manager.getCooldownInfo(ctx.message().uuid);
                if (info == null) {
                    Dispatcher.sendToClient(
                            new ItemCooldownInfoResponsePacket(
                                    true,
                                    -1,
                                    null
                            ),
                            ctx.sender()
                    );
                    return;
                }
                Dispatcher.sendToClient(new ItemCooldownInfoResponsePacket(
                        false,
                        ItemCooldownPackets.COOLDOWN_ID_MAPPING.inverse().get(info.getClass()),
                        info
                ), ctx.sender());
            } else {
                Dispatcher.sendToClient(
                        new ItemCooldownInfoResponsePacket(
                                true,
                                -1,
                                null
                        ),
                        ctx.sender()
                );
            }
        }
    }
}
