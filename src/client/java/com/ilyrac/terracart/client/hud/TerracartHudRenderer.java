package com.ilyrac.terracart.client.hud;

import com.ilyrac.terracart.entity.TerracartEntity;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class TerracartHudRenderer {

    public static final HudElement STATS_DISPLAY = (context, _) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !(mc.player.getVehicle() instanceof TerracartEntity cart)) return;

        Font font = mc.font;
        int screenW = context.guiWidth();
        int screenH = context.guiHeight();

        int boxWidth = 95;
        int boxHeight = 45;
        int xLeft = screenW - boxWidth;
        int yCenter = screenH / 2;
        int yTop = yCenter - (boxHeight / 2);

        // 1. Draw Background
        context.fill(xLeft, yTop, screenW, yTop + boxHeight, 0x40000000);

        int labelX = xLeft + 5;
        int valueX = screenW - 5;

        // Row 1: Speed
        String speedVal = String.format("%.1f", cart.getSpeedBps()) + " b/s";
        context.text(font, "Speed:", labelX, yCenter - 15, 0xFFAAAAAA);
        context.text(font, speedVal, valueX - font.width(speedVal), yCenter - 15, 0xFFFFFFFF);

        // Row 2: Fuel
        String fuelVal = Math.round(cart.getFuelPercent() * 100.0f) + "%";
        context.text(font, "Fuel:", labelX, yCenter - 3, 0xFFAAAAAA);
        context.text(font, fuelVal, valueX - font.width(fuelVal), yCenter - 3, 0xFFFFFFFF);

        // Row 3: Health
        String healthVal = Math.round(cart.getHealthPercent() * 100.0f) + "%";
        context.text(font, "Condition:", labelX, yCenter + 9, 0xFFAAAAAA);
        context.text(font, healthVal, valueX - font.width(healthVal), yCenter + 9, 0xFFFFFFFF);
    };
}