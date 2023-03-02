package com.redpxnda.aegis.forge.client;

import com.redpxnda.aegis.client.DefenseShieldOverlay;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ForgeDefenseShieldOverlay {
    public static final IGuiOverlay HUD_SHIELD = ((gui, poseStack, partialTick, width, height) -> {
        DefenseShieldOverlay.render(width, height, poseStack, gui.getMinecraft());
    });
}
