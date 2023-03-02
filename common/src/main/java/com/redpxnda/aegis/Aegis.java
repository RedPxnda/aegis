package com.redpxnda.aegis;

import com.redpxnda.aegis.network.ModPackets;
import com.redpxnda.aegis.registry.ModRegistries;

public class Aegis {
    public static final String MOD_ID = "aegis";

    public static void init() {
        ModRegistries.init();
        ModPackets.init();
    }
}
