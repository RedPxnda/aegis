package com.redpxnda.aegis.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.aegis.mixin.TextureAtlasSpriteMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

import static com.redpxnda.aegis.Aegis.MOD_ID;
import static com.redpxnda.aegis.util.RenderUtils.getPixelRGBA;

public class DefenseShieldOverlay {
    public static final ResourceLocation SHIELD_ICON = new ResourceLocation(MOD_ID, "textures/gui/shield.png");
    public static final ResourceLocation SHIELD_ICON_SPRITE = new ResourceLocation(MOD_ID, "gui/shield");
    public static final ResourceLocation EMPTY_SHIELD_ICON_SPRITE = new ResourceLocation(MOD_ID, "gui/shield_empty");

    public static boolean render(int width, int height, PoseStack poseStack, Minecraft minecraft) {
        if (minecraft.gameMode != null && // doing the checks to make sure the display should be visible
                !minecraft.options.hideGui && // if f1 isnt enabled
                minecraft.gameMode.canHurtPlayer() && // if not creative/spectator
                minecraft.getCameraEntity() instanceof Player) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f); // set color to white
            if (minecraft.player == null) return false;
            ItemStack armorPiece = minecraft.player.getInventory().getArmor(2); // get player's chestplate
            if (!(armorPiece == ItemStack.EMPTY)) { // unless player does not have a chestplate, run 👇
                BakedModel model = minecraft.getItemRenderer().getModel(armorPiece, minecraft.level, minecraft.player, 0); // get chestplate item model
                if (minecraft.level == null || minecraft.level.random == null) return false;
                TextureAtlasSprite sprite = model.getQuads(null, null, minecraft.level.getRandom()).get(0).getSprite(); // get chestplate item model sprite
                Color color = new Color(getPixelRGBA(8, 8, sprite), true); // since 👆 returns an integer color, I have to separate red, green, and blue
                RenderSystem.setShaderColor(color.getBlue()/255f, color.getGreen()/255f, color.getRed()/255f, 1.0f); // for some reason red and blue are swapped, god knows why
            }
            RenderSystem.setShader(GameRenderer::getPositionTexShader); // setting the shader
            RenderSystem.setShaderTexture(0, SHIELD_ICON); // setting the texture
            int x = (minecraft.options.mainHand().get().equals(HumanoidArm.LEFT)) ? width/2 - 108 : width/2 + 93; // x position is opposite of main arm, to prevent interference with offhand display
            GuiComponent.blit(poseStack, x, height-20, 0, 0, 16, 16, 16, 16); // aannnnd actually rendering it
            return true; // true if successful
        }
        return false; // false if not successful
    }
}
