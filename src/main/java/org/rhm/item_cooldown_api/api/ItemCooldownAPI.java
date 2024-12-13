package org.rhm.item_cooldown_api.api;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rhm.item_cooldown_api.ItemCooldownAPICommon;
import org.rhm.item_cooldown_api.component.CooldownComponent;
import org.rhm.item_cooldown_api.component.ItemCooldownComponents;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * API for managing item cooldowns.
 */
public class ItemCooldownAPI {
    /**
     * Retrieves the cooldown component for the specified item stack.
     *
     * @param stack the item stack
     * @return the cooldown component or null if the stack doesn't have one
     */
    @Nullable
    public static CooldownComponent getItemCooldown(@NotNull ItemStack stack) {
        //? if >=1.20.5 {
        if (stack.has(ItemCooldownComponents.COOLDOWN_COMPONENT.get()))
            return stack.get(ItemCooldownComponents.COOLDOWN_COMPONENT.get());
        //?} else {
        /*CompoundTag cooldown = stack.getTagElement("Cooldown");
        if (cooldown != null) {
            DataResult<Pair<CooldownComponent, Tag>> result = CooldownComponent.CODEC.decode(NbtOps.INSTANCE, cooldown);
            if (result.error().isPresent() || result.result().isEmpty()) {
                return null;
            }
            return result.result().get().getFirst();
        }
        *///?}
        return null;
    }

    /**
     * Sets a cooldown for an item stack. Optionally, a player UUID can be provided for per-player cooldowns.
     *
     * @param stack the item stack
     * @param playerUuid the player's UUID (nullable for shared cooldowns)
     */
    public static void setCooldown(@NotNull ItemStack stack, @Nullable UUID playerUuid) {
        CooldownComponent component = getItemCooldown(stack);
        
        if (component == null) {
            ItemCooldownAPICommon.logger.error("Failed to retrieve cooldown component for stack: {}", stack);
            return;
        }
        CooldownInfo info;
        if (component.perPlayer()) {
            if (playerUuid == null) {
                ItemCooldownAPICommon.logger.error("Player UUID is null while setting per-player cooldown for stack: {}", stack);
                return;
            }
            info = new IndividualCooldownInfo(
                    ItemCooldownAPICommon.manager.newId(),
                    Map.of(playerUuid, component.cooldown())
            );
        } else {
            info = new SharedCooldownInfo(
                    ItemCooldownAPICommon.manager.newId(),
                    component.cooldown()
            );
        }
        ItemCooldownAPICommon.manager.addCooldown(info);
        component = component.withCooldown(info.getCooldownId());
        //? if >=1.20.5 {
        stack.set(ItemCooldownComponents.COOLDOWN_COMPONENT.get(), component);
        //?} else {
        /*CompoundTag tag = stack.getTag();
        if (tag != null) {
            DataResult<Tag> result = CooldownComponent.CODEC.encodeStart(NbtOps.INSTANCE, component);
            if (result.error().isPresent() || result.result().isEmpty()) {
                ItemCooldownAPICommon.logger.warn("Could not save cooldown with id {} on item.", info.getCooldownId());
            } else {
                tag.put("Cooldown", result.result().get());
            }
            stack.setTag(tag);
        }
        *///?}
    }

    /**
     * Gets the progress of the cooldown for a specified item stack.
     *
     * @param item the item stack
     * @return the cooldown progress (0.0 = no cooldown, 1.0 = fully on cooldown)
     */
    public static float getCooldownProgress(@NotNull ItemStack item) {
        return getCooldownProgress(getItemCooldown(item), null);
    }
    /**
     * Gets the progress of the cooldown for a specified item stack.
     *
     * @param component the cooldown component
     * @return the cooldown progress (0.0 = no cooldown, 1.0 = fully on cooldown)
     */
    public static float getCooldownProgress(@Nullable CooldownComponent component) {
        return getCooldownProgress(component, null);
    }
    /**
     * Gets the progress of the cooldown for a specified item stack and optionally a player UUID.
     *
     * @param item the item stack
     * @param playerUuid the player's UUID (nullable if the cooldown is shared)
     * @return the cooldown progress (0.0 = no cooldown, 1.0 = fully on cooldown)
     */
    public static float getCooldownProgress(@NotNull ItemStack item, @Nullable UUID playerUuid) {
        return getCooldownProgress(getItemCooldown(item), playerUuid);
    }
    /**
     * Gets the progress of the cooldown for a specified component and optionally a player UUID.
     *
     * @param component the cooldown component
     * @param playerUuid the player's UUID (nullable if the cooldown is shared)
     * @return the cooldown progress (0.0 = no cooldown, 1.0 = fully on cooldown)
     */
    public static float getCooldownProgress(@Nullable CooldownComponent component, @Nullable UUID playerUuid) {
        return ItemCooldownAPICommon.manager.getCooldownProgress(component, playerUuid);
    }

    /**
     * Checks if the specified item stack is currently on cooldown.
     *
     * @param item the item stack
     * @return true if the item is on cooldown, false otherwise
     */
    public static boolean isItemOnCooldown(@NotNull ItemStack item) {
        return isItemOnCooldown(getItemCooldown(item), null);
    }
    /**
     * Checks if the specified cooldown component is currently on cooldown.
     *
     * @param component the cooldown component
     * @return true if the item is on cooldown, false otherwise
     */
    public static boolean isItemOnCooldown(@Nullable CooldownComponent component) {
        return isItemOnCooldown(component, null);
    }
    /**
     * Checks if the specified item stack is currently on cooldown, considering an optional player UUID.
     *
     * @param item the item stack
     * @param playerUuid the player's UUID (nullable if the cooldown is shared)
     * @return true if the item is on cooldown, false otherwise
     */
    public static boolean isItemOnCooldown(@NotNull ItemStack item, @Nullable UUID playerUuid) {
        return isItemOnCooldown(getItemCooldown(item), playerUuid);
    }
    /**
     * Checks if the specified cooldown component is currently on cooldown, considering an optional player UUID.
     *
     * @param component the cooldown component
     * @param playerUuid the player's UUID (nullable if the cooldown is shared)
     * @return true if the item is on cooldown, false otherwise
     */
    public static boolean isItemOnCooldown(@Nullable CooldownComponent component, @Nullable UUID playerUuid) {
        return ItemCooldownAPICommon.manager.isOnCooldown(component, playerUuid);
    }

    /**
     * Abstract class representing the information about an item's cooldown.
     */
    public abstract static class CooldownInfo {
        private final UUID cooldownId;

        public CooldownInfo(@NotNull UUID cooldownId) {
            this.cooldownId = cooldownId;
        }

        /**
         * Tick the cooldown, reducing the remaining time.
         */
        public abstract void tick();
        /**
         * Check if the cooldown is active for the given player UUID.
         *
         * @param uuid the player's UUID (nullable)
         * @return true if the cooldown is active, false otherwise
         */
        public abstract boolean onCooldown(@Nullable UUID uuid);
        /**
         * Determines if this cooldown information should be removed (i.e., if the cooldown has expired).
         *
         * @return true if the cooldown should be removed, false otherwise
         */
        public abstract boolean shouldRemove();
        /**
         * Encodes the cooldown info into a FriendlyByteBuf for network transmission.
         *
         * @param buf the buffer to write the data to
         */
        public abstract void encode(FriendlyByteBuf buf);
        /**
         * Converts the cooldown info to a CompoundTag for NBT storage.
         *
         * @return the NBT representation of this cooldown info
         */
        public abstract CompoundTag toNbt();

        @NotNull
        public UUID getCooldownId() {
            return cooldownId;
        }
    }

    /**
     * Represents cooldown information for individual players.
     * Each player has their own cooldown time.
     */
    public static class IndividualCooldownInfo extends CooldownInfo {
        private final Map<UUID, Long> remaining;

        public IndividualCooldownInfo(@NotNull UUID cooldownId, @NotNull Map<UUID, Long> remaining) {
            super(cooldownId);
            this.remaining = remaining;
        }

        /**
         * Gets the remaining cooldown time for a specific player.
         *
         * @param uuid the player's UUID (nullable)
         * @return the remaining time, or 0 if the player is not on cooldown (or the uuid is null)
         */
        public long getRemaining(@Nullable UUID uuid) {
            return remaining.getOrDefault(uuid, 0L);
        }

        @Override
        public void tick() {
            for (UUID uuid : remaining.keySet()) {
                long currentRemaining = remaining.get(uuid)-1;
                if (currentRemaining <= 0)
                    remaining.remove(uuid);
                else
                    remaining.put(uuid, currentRemaining);
            }
        }

        @Override
        public boolean onCooldown(@Nullable UUID uuid) {
            return remaining.getOrDefault(uuid,0L) >= 1;
        }

        @Override
        public boolean shouldRemove() {
            return remaining.isEmpty();
        }

        @Override
        public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(getCooldownId());
            buf.writeInt(remaining.size());
            remaining.forEach((uuid, time) -> {
                buf.writeUUID(uuid);
                buf.writeLong(time);
            });
        }

        @Override
        public CompoundTag toNbt() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("Id", getCooldownId());

            ListTag cooldowns = new ListTag();

            this.remaining.forEach((uuid, time) -> {
                CompoundTag tag1 = new CompoundTag();
                tag1.putUUID("Id", uuid);
                tag1.putLong("Remaining", time);
                cooldowns.add(tag1);
            });

            tag.put("Cooldowns", cooldowns);
            return tag;
        }

        public static IndividualCooldownInfo decode(FriendlyByteBuf buf) {
            UUID cooldownId = buf.readUUID();
            int size = buf.readInt();
            HashMap<UUID, Long> remaining = new HashMap<>(size);

            for (int i = 0; i < size; i++) {
                remaining.put(buf.readUUID(), buf.readLong());
            }

            return new IndividualCooldownInfo(cooldownId, remaining);
        }

        @Nullable
        public static IndividualCooldownInfo fromNbt(CompoundTag tag) {
            if (!tag.hasUUID("Id")) {
                ItemCooldownAPICommon.logger.error("Missing UUID 'Id' in NBT tag: {}", tag);
                return null;
            }
            UUID uuid = tag.getUUID("Id");
            HashMap<UUID, Long> cooldowns = new HashMap<>();
            if (!tag.contains("Cooldowns")) {
                ItemCooldownAPICommon.logger.warn("Missing 'Cooldowns' list in NBT tag: {}", tag);
                return new IndividualCooldownInfo(
                        uuid,
                        cooldowns
                );
            }
            for (Tag cooldown : tag.getList("Cooldowns", Tag.TAG_COMPOUND)) {
                CompoundTag tag1 = (CompoundTag) cooldown;
                if (!tag1.hasUUID("Id") || !tag1.contains("Remaining")) {
                    ItemCooldownAPICommon.logger.warn("Incomplete cooldown entry in NBT tag: {}. Missing 'Id' or 'Remaining'.", tag1);
                } else {
                    cooldowns.put(tag1.getUUID("Id"), tag1.getLong("Remaining"));
                }
            }
            return new IndividualCooldownInfo(
                    uuid,
                    cooldowns
            );
        }
    }

    /**
     * Represents cooldown information for a shared cooldown between all players.
     * All players share the same cooldown time.
     */
    public static class SharedCooldownInfo extends CooldownInfo {
        protected long remaining;

        public SharedCooldownInfo(@NotNull UUID cooldownId, long remaining) {
            super(cooldownId);
            this.remaining = remaining;
        }

        /**
         * Gets the remaining cooldown time for the shared cooldown.
         *
         * @return the remaining cooldown time
         */
        public long getRemaining() {
            return remaining;
        }

        @Override
        public void tick() {
            if (remaining > 0) remaining--;
        }

        @Override
        public boolean onCooldown(@Nullable UUID uuid) {
            return remaining >= 1;
        }

        @Override
        public boolean shouldRemove() {
            return remaining < 1;
        }

        @Override
        public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(getCooldownId());
            buf.writeLong(remaining);
        }

        @Override
        public CompoundTag toNbt() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("Id", getCooldownId());
            tag.putLong("Remaining", remaining);

            return tag;
        }

        public static SharedCooldownInfo decode(FriendlyByteBuf buf) {
            return new SharedCooldownInfo(
                    buf.readUUID(),
                    buf.readLong()
            );
        }

        @Nullable
        public static SharedCooldownInfo fromNbt(CompoundTag tag) {
            if (!tag.hasUUID("Id")) {
                ItemCooldownAPICommon.logger.error("Missing UUID 'Id' in NBT tag: {}", tag);
                return null;
            }
            long cooldown = 0L;
            if (!tag.contains("Remaining")) {
                ItemCooldownAPICommon.logger.warn("Missing 'Remaining' field in NBT tag: {}", tag);
            } else
                cooldown = tag.getLong("Remaining");
            return new SharedCooldownInfo(
                    tag.getUUID("Id"),
                    cooldown
            );
        }
    }
}
