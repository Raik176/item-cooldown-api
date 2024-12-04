package org.rhm.item_cooldown_api.component;

import net.minecraft.core.component.DataComponentType;
import org.rhm.item_cooldown_api.ItemCooldownAPICommon;

public class ItemCooldownComponents {
    public static final DataComponentType<CooldownComponent> COOLDOWN_COMPONENT = ItemCooldownAPICommon
            .getImplementation().registerDataComponentType(
                    "cooldown",
                    DataComponentType.<CooldownComponent>builder()
                            .build()
            );

    public static void init() { }

    public record StoredCooldownComponent() {

    }
    public record CooldownComponent(long cooldown, boolean persistent, boolean perPlayer) {

    }
}
