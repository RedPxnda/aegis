package com.redpxnda.aegis;

import com.redpxnda.aegis.data.listener.AttributeData;
import com.redpxnda.aegis.network.ModPackets;
import com.redpxnda.aegis.registry.ModRegistries;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.server.packs.PackType;

public class Aegis {
    public static final String MOD_ID = "aegis";

    public static void init() {
        ModRegistries.init();
        ModPackets.init();

        ReloadListenerRegistry.register(PackType.SERVER_DATA, new AttributeData());
    }
}
