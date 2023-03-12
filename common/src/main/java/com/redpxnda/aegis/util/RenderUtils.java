package com.redpxnda.aegis.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.redpxnda.aegis.mixin.TextureAtlasSpriteAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class RenderUtils {
    public static void addVertex(Matrix4f pPose, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, float pX, float pY, float pZ, float pU, float pV, int light) {
        pConsumer.vertex(pPose, pX, pY, pZ).color(pRed, pGreen, pBlue, pAlpha).uv(pU, pV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
    }

    public static Color mostCommon(ArrayList<Color> list) {
        Map<Color, Integer> map = new HashMap<>(); // map of each color and its count

        for (Color t : list) {
            Integer val = map.get(t); // value/count: get already existing count
            map.put(t, val == null ? 1 : val + 1); // set the color's count to: count+1, (or if there is no count) 1
        }

        Map.Entry<Color, Integer> max = null;
        for (Map.Entry<Color, Integer> e : map.entrySet()) { // don't feel like thoroughly explaining this, but it essentially compares the color to the previous- and if the previous is more common, set the current color to the previous color
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }
        if (max == null) return null;
        return max.getKey();
    }

    public static int getPixelRGBA(int x, int y, TextureAtlasSprite sprite) {
        return ((TextureAtlasSpriteAccessor) sprite).getMainImage()[0].getPixelRGBA(x, y); // using my TextureAtlasSprite accessor mixin, I can get the RGBA of a pixel
    }
}
