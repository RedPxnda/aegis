package com.redpxnda.aegis.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientShieldData {
    private static double shield;

    public static void setShield(double amt) {
        shield = amt;
    }

    public static double getShield() {
        return shield;
    }

}
