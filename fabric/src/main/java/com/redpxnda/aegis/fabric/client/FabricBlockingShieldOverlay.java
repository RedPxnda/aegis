package com.redpxnda.aegis.fabric.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.aegis.client.BlockingShieldOverlay;
import com.redpxnda.aegis.client.DefenseShieldOverlay;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class FabricBlockingShieldOverlay implements HudRenderCallback {
    @Override
    public void onHudRender(PoseStack matrixStack, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        BlockingShieldOverlay.render(window.getGuiScaledWidth(), window.getGuiScaledHeight(), matrixStack, mc);
    }
}
