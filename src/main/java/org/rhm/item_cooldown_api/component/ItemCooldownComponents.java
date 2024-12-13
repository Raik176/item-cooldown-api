package org.rhm.item_cooldown_api.component;

import org.rhm.item_cooldown_api.ItemCooldownAPICommon;

import java.util.function.Supplier;


//? if >=1.20.5
import net.minecraft.core.component.DataComponentType;

public class ItemCooldownComponents {
    //? if >=1.20.5 {
    public static final Supplier<DataComponentType<CooldownComponent>> COOLDOWN_COMPONENT = ItemCooldownAPICommon
            .getImplementation().registerDataComponentType(
                    "cooldown",
                    DataComponentType.<CooldownComponent>builder()
                            .persistent(CooldownComponent.CODEC)
                            .build()
            );
    //?}

    public static void init() { }
}
