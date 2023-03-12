package com.redpxnda.aegis.forge.client;

import com.redpxnda.aegis.client.BlockingShieldOverlay;
import com.redpxnda.aegis.client.DefenseShieldOverlay;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ForgeBlockingShieldOverlay {
    public static final IGuiOverlay HUD_SHIELD = ((gui, poseStack, partialTick, width, height) -> {
        BlockingShieldOverlay.render(width, height, poseStack, gui.getMinecraft());
    });
}
