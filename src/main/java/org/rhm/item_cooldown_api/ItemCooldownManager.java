package org.rhm.item_cooldown_api;

import commonnetwork.api.Dispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.rhm.item_cooldown_api.api.ItemCooldownAPI;
import org.rhm.item_cooldown_api.component.CooldownComponent;
import org.rhm.item_cooldown_api.packet.RequestItemCooldownInfoPacket;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@ApiStatus.Internal
public class ItemCooldownManager {
    private HashMap<UUID, ItemCooldownAPI.CooldownInfo> cooldowns;
    private final Path cooldownsPath = ItemCooldownAPICommon.getImplementation().getConfigPath()
            .resolve(ItemCooldownAPICommon.MOD_ID).resolve("cooldowns.nbt");

    public void save(MinecraftServer server) {
        CompoundTag cooldownRoot = new CompoundTag();
        ListTag cooldowns = new ListTag();

        for (ItemCooldownAPI.CooldownInfo value : this.cooldowns.values()) {
            CompoundTag tag = value.toNbt();
            tag.putInt("CooldownId", CooldownId.getId(value.getClass()).getId());
            cooldowns.add(tag);
        }

        cooldownRoot.put("", cooldowns);
        try {
            Files.createDirectories(cooldownsPath.getParent());
            Files.deleteIfExists(cooldownsPath);
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(cooldownsPath.toFile()));
            NbtIo.write(cooldownRoot, stream);
            stream.flush();
            stream.close();
        } catch (IOException exception) {
            ItemCooldownAPICommon.logger.error("Failed to save cooldown data to file: {}", cooldownsPath, exception);
        }
    }

    public void load(MinecraftServer server) {
        cooldowns = new HashMap<>();
        if (!Files.exists(cooldownsPath)) return;
        try {
            DataInputStream stream = new DataInputStream(new FileInputStream(cooldownsPath.toFile()));
            CompoundTag tag = NbtIo.read(stream);
            stream.close();
            if (tag.contains("")) {
                for (Tag tag1 : tag.getList("", Tag.TAG_COMPOUND)) {
                    CompoundTag compoundTag = (CompoundTag)tag1;
                    ItemCooldownAPI.CooldownInfo info = CooldownId.fromId(compoundTag.getInt("CooldownId")).fromNbt(compoundTag);
                    if (info != null)
                        cooldowns.put(info.getCooldownId(), info);
                    else {
                        ItemCooldownAPICommon.logger.warn("Failed to load cooldown data: Invalid cooldown info for Id {}", compoundTag.getInt("CooldownId"));
                    }
                }
            }
        } catch (IOException exception) {
            ItemCooldownAPICommon.logger.error("Failed to load cooldown data from file: {}", cooldownsPath, exception);
        }
    }

    public void tick() {
        Iterator<ItemCooldownAPI.CooldownInfo> iterator = cooldowns.values().iterator();
        while (iterator.hasNext()) {
            ItemCooldownAPI.CooldownInfo cooldownInfo = iterator.next();
            cooldownInfo.tick();
            if (cooldownInfo.shouldRemove()) {
                iterator.remove();
            }
        }
    }

    private ItemCooldownAPI.CooldownInfo getOrRequestInfo(UUID cooldownId) {
        if (cooldowns.containsKey(cooldownId))
            return cooldowns.get(cooldownId);
        if (cooldownId != null)
            Dispatcher.sendToServer(new RequestItemCooldownInfoPacket(cooldownId));

        return null;
    }


    // will only be executed on server so no need to use getOrRequestInfo
    public boolean cooldownExists(UUID uuid) {
        return cooldowns.containsKey(uuid);
    }
    public ItemCooldownAPI.CooldownInfo getCooldownInfo(UUID cooldownId) {
        return cooldowns.getOrDefault(cooldownId, null);
    }


    public boolean isOnCooldown(CooldownComponent component, UUID uuid) {
        if (component == null) return false;
        return isOnCooldown(component.currentCooldown().orElse(null),uuid);
    }

    public boolean isOnCooldown(UUID cooldownId, UUID uuid) {
        if (cooldownId == null) return false;
        ItemCooldownAPI.CooldownInfo info = getOrRequestInfo(cooldownId);

        if (info == null) {
            return false;
        } else if (info instanceof ItemCooldownAPI.SharedCooldownInfo) {
            return true;
        } else if (info instanceof ItemCooldownAPI.IndividualCooldownInfo individualCooldownInfo) {
            if (uuid == null) {
                ItemCooldownAPICommon.logger.error("UUID is null while checking individual cooldown for cooldown with UUID: {}", cooldownId);
                return false;
            }
            return individualCooldownInfo.onCooldown(uuid);
        } else {
            ItemCooldownAPICommon.logger.warn("Unexpected cooldown type encountered for cooldown with UUID: {}. Info: {}", cooldownId, info);
        }
        return false;
    }

    public void addCooldown(ItemCooldownAPI.CooldownInfo info) {
        cooldowns.put(info.getCooldownId(), info);
    }

    public float getCooldownProgress(CooldownComponent component, UUID uuid) {
        if (component == null) return 0.0f;

        ItemCooldownAPI.CooldownInfo info = getOrRequestInfo(component.currentCooldown().orElse(null));

        if (info == null) {
            return 0.0f;
        } else if (info instanceof ItemCooldownAPI.SharedCooldownInfo sharedCooldownInfo) {
            return (float) sharedCooldownInfo.getRemaining() / component.cooldown();
        } else if (info instanceof ItemCooldownAPI.IndividualCooldownInfo individualCooldownInfo) {
            if (uuid == null) {
                ItemCooldownAPICommon.logger.error("UUID is null while calculating cooldown progress for cooldown: {}", component);
                return 0.0f;
            }
            return (float) individualCooldownInfo.getRemaining(uuid) / component.cooldown();
        } else {
            ItemCooldownAPICommon.logger.warn("Unexpected cooldown type encountered while calculating cooldown progress for component: {}. Info: {}",
                    component, info);
        }
        return 0.0f;
    }

    public UUID newId() {
        UUID newId;
        do {
            newId = UUID.randomUUID();
        } while (cooldowns.containsKey(newId));
        return newId;
    }

    @ApiStatus.Internal
    public enum CooldownId {
        NONE(-1, ItemCooldownAPI.CooldownInfo.class),
        INDIVIDUAL(0, ItemCooldownAPI.IndividualCooldownInfo.class),
        SHARED(1, ItemCooldownAPI.SharedCooldownInfo.class);

        private final int id;
        private final Class<? extends ItemCooldownAPI.CooldownInfo> clazz;

        CooldownId(int id, Class<? extends ItemCooldownAPI.CooldownInfo> clazz) {
            this.id = id;
            this.clazz = clazz;
        }

        public int getId() {
            return id;
        }

        public static CooldownId fromId(int id) {
            return Arrays.stream(CooldownId.values()).filter(i -> i.getId() == id)
                    .findFirst().orElse(CooldownId.NONE);
        }

        public ItemCooldownAPI.CooldownInfo decode(FriendlyByteBuf buf) {
            if (this == CooldownId.NONE) {
                return null;
            } else if (id == 0) {
                return ItemCooldownAPI.IndividualCooldownInfo.decode(buf);
            } else if (id == 1) {
                return ItemCooldownAPI.SharedCooldownInfo.decode(buf);
            } else {
                ItemCooldownAPICommon.logger.warn("Unexpected id encountered during decoding. Id: {}", id);
                return null;
            }
        }

        public ItemCooldownAPI.CooldownInfo fromNbt(CompoundTag tag) {
            if (this == CooldownId.NONE) {
                return null;
            } else if (id == 0) {
                return ItemCooldownAPI.IndividualCooldownInfo.fromNbt(tag);
            } else if (id == 1) {
                return ItemCooldownAPI.SharedCooldownInfo.fromNbt(tag);
            } else {
                ItemCooldownAPICommon.logger.warn("Unexpected id encountered during NBT parsing. Id: {}", id);
                return null;
            }
        }

        public boolean isValid(ItemCooldownAPI.CooldownInfo info) {
            if (info == null) return this == CooldownId.NONE;
            return clazz.equals(info.getClass());
        }

        public static CooldownId getId(Class<? extends ItemCooldownAPI.CooldownInfo> clazz) {
            return Arrays.stream(CooldownId.values()).filter(id -> id.clazz.equals(clazz))
                    .findFirst().orElse(CooldownId.NONE);
        }
    }
}
