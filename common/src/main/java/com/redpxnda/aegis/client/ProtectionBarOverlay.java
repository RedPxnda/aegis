package com.redpxnda.aegis.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.aegis.registry.ModRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static com.redpxnda.aegis.Aegis.MOD_ID;

public class ProtectionBarOverlay {
    private static final ResourceLocation PROTECTION_ICON = new ResourceLocation(MOD_ID, "textures/gui/protection.png");

    public static boolean render(int width, int height, PoseStack poseStack, boolean shouldDraw, boolean hideGui) {
        int x, y;
        x = width/2-91;
        y = height;
        Player player = Minecraft.getInstance().player;
        if (player != null && shouldDraw && !hideGui) { // making sure the render should actually be drawn
            poseStack.translate(0, 0, -5); // moving the pose back 5 units on the z, so it renders behind chat and what not (doesn't seem to work on fabric?)
            RenderSystem.setShader(GameRenderer::getPositionTexShader); // shader
            double overallProt = player.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("overall_protection").get()); // overall protection attribute
            double frontProt = player.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("frontal_protection").get()); // frontal protection attribute
            double rearProt = player.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("rearwards_protection").get()); // rearwards protection attribute
            double sideProt = player.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("sidewards_protection").get()); // sidewards protection attribute
            double avg = ((overallProt+frontProt)+(overallProt+rearProt)+(overallProt+sideProt))/3; // average of all 3
            double reductionPercent = Math.round((avg / (avg+20))*1000); // getting reduction percent, except rounded
            reductionPercent/=10;
            if (reductionPercent > 0) { // if the player actually has protection
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f); // setting color
                GuiComponent.drawString(poseStack, Minecraft.getInstance().font, reductionPercent + "%", x + 52, y, 16777215); // drawing reduction percent text
                for (int i = 0; i < 6; i++) { // 6 times, twice for each protection type
                    double side = overallProt;
                    switch (i) { // i'm too lazy to think of a mathematical way to represent this, so I use a switch statement
                        case 0, 1 -> side += frontProt; // first 2 icons represent front
                        case 2, 3 -> side += rearProt; // next 2 represent back
                        case 4, 5 -> side += sideProt; // last 2 represent side
                    }
                    double applyAmount = Math.abs(side - avg) / (Math.abs(side - avg) + 10); // comparing each side to the average
                    float redDenom = (side - avg > 0) ? (float) applyAmount : 0f; // determining red based on 👆
                    float greenDenom = (side - avg < 0) ? (float) applyAmount : 0f; // 👆 but green
                    RenderSystem.setShaderTexture(0, PROTECTION_ICON); // setting texture
                    RenderSystem.setShaderColor(1 - redDenom, 1 - greenDenom, 0f, 1f); // setting color
                    GuiComponent.blit(poseStack, x + i * 8, y, 0, 0, 9, 9, 9, 9); // actually rendering 😀
                }
                return true; // true if successful
            }
        }
        return false; // false if not
    }
}
