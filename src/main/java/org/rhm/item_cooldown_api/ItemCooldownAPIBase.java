package org.rhm.item_cooldown_api;


import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.function.Supplier;


//? if >=1.20.5
import net.minecraft.core.component.DataComponentType;

@ApiStatus.Internal
public interface ItemCooldownAPIBase {
    //? if >=1.20.5
    <T> Supplier<DataComponentType<T>> registerDataComponentType(String id, DataComponentType<T> type);
    Path getConfigPath();
}
