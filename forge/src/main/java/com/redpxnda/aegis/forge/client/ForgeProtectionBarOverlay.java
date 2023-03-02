package com.redpxnda.aegis.forge.client;

import com.redpxnda.aegis.client.ProtectionBarOverlay;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ForgeProtectionBarOverlay {
    public static final IGuiOverlay HUD_PROTECTION = ((gui, poseStack, partialTick, width, height) -> {
        boolean render = ProtectionBarOverlay.render(width, height-gui.leftHeight, poseStack, gui.shouldDrawSurvivalElements(), gui.getMinecraft().options.hideGui);
        if (render) gui.leftHeight+=10; // forge's field for measuring icon offsets, fabric is silly and doesn't have an equivalent, so I have to calculate it myself over there
    });
}
