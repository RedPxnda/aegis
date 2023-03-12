package com.redpxnda.aegis.util;

import com.redpxnda.aegis.client.ShieldIcons;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

import static com.redpxnda.aegis.client.ShieldIcons.*;

public class PlayerShieldIcon {
    private static Map<String, ShieldIcons> playerIcons = Map.ofEntries(
            Map.entry("RedPxnda", ROYAL),
            Map.entry("Dev", ROYAL)
    );

    public static ShieldIcons getPlayersIcon(String player) {
        return playerIcons.getOrDefault(player, CLASSIC);
    }

    public static ShieldIcons getPlayersIcon(Player player) {
        return playerIcons.getOrDefault(player.getName().getString(), CLASSIC);
    }
}
