package org.rhm.item_cooldown_api.mixin;

import net.minecraft.server.MinecraftServer;
import org.rhm.item_cooldown_api.ItemCooldownAPICommon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class) // fabric api <= 1.20.1 doesn't have the save event (i think?), so i just mixin.
public class MinecraftServerMixin {
    @Inject(method = "saveAllChunks", at = @At("TAIL"))
    private void save(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
        ItemCooldownAPICommon.onSave((MinecraftServer)(Object)this);
    }
}
