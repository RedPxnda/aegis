package com.redpxnda.aegis.fabric.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.aegis.client.ProtectionBarOverlay;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class FabricProtectionBarOverlay implements HudRenderCallback {
    @Override
    public void onHudRender(PoseStack matrixStack, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        boolean shouldDraw = mc.gameMode != null && mc.gameMode.canHurtPlayer() && mc.getCameraEntity() instanceof Player;
        Player player = mc.player;
        if (player == null) return;
        boolean hideGui = mc.options.hideGui;
        // 👇 minecraft's code, I don't know exactly what it does- only that it is used to calculate the position of the armor bar based on the player's health.
        float f = Math.max(player.getMaxHealth(), player.getHealth());
        int p = Mth.ceil(player.getAbsorptionAmount());
        int q = Mth.ceil((f + (float)p) / 2.0f / 10.0f);
        int r = Math.max(10 - (q - 2), 3);
        ProtectionBarOverlay.render(window.getGuiScaledWidth(), window.getGuiScaledHeight()-39 - (q - 1) * r - 10, matrixStack, shouldDraw, hideGui);
    }
}
