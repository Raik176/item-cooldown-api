package org.rhm.item_cooldown_api.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record CooldownComponent(long cooldown, boolean perPlayer, Optional<UUID> currentCooldown) {
    public static final Codec<CooldownComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.LONG.fieldOf("cooldown").forGetter(CooldownComponent::cooldown),
            Codec.BOOL.optionalFieldOf("perPlayer", false).forGetter(CooldownComponent::perPlayer),
            UUIDUtil.CODEC.optionalFieldOf("cooldownId").forGetter(CooldownComponent::currentCooldown)
    ).apply(builder, CooldownComponent::new));

    public CooldownComponent(long cooldown) {
        this(cooldown, false);
    }

    public CooldownComponent(long cooldown, boolean perPlayer) {
        this(cooldown, perPlayer, Optional.empty());
    }

    public CooldownComponent(long cooldown, boolean perPlayer, @Nullable UUID currentCooldown) {
        this(cooldown, perPlayer, Optional.ofNullable(currentCooldown));
    }

    public CooldownComponent(long cooldown, boolean perPlayer, Optional<UUID> currentCooldown) {
        this.cooldown = cooldown;
        this.perPlayer = perPlayer;
        this.currentCooldown = currentCooldown;
    }

    @NotNull
    public CooldownComponent withCooldown(@NotNull UUID cooldownId) {
        return new CooldownComponent(
                cooldown,
                perPlayer,
                Optional.of(cooldownId)
        );
    }
}