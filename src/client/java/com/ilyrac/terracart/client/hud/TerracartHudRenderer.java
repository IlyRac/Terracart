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

        // --- HUD DATA SETUP ---
        String speedVal = String.format("%.1f", cart.getSpeedBps()) + " b/s";
        String fuelVal = Math.round(cart.getFuelPercent() * 100.0f) + "%";
        String healthVal = Math.round(cart.getHealthPercent() * 100.0f) + "%";

        String[] labels = {"Speed:", "Fuel:", "Condition:"};
        String[] values = {speedVal, fuelVal, healthVal};

        // --- STYLING CONSTANTS ---
        int paddingH = 6;
        int paddingV = 4;
        int rowHeight = 9;
        int margin = 5;

        // Dynamic width calculation
        int maxLabelWidth = 0;
        for (String label : labels) {
            maxLabelWidth = Math.max(maxLabelWidth, font.width(label));
        }

        int maxValueWidth = 0;
        for (String value : values) {
            maxValueWidth = Math.max(maxValueWidth, font.width(value));
        }

        // Box size math
        int boxWidth = paddingH * 3 + maxLabelWidth + maxValueWidth;
        int boxHeight = paddingV * 2 + rowHeight * labels.length;

        // --- POSITIONING (Vertically Centered, Right Aligned) ---
        int xLeft = screenW - boxWidth - margin;
        int xRight = xLeft + boxWidth;
        int yTop = (screenH - boxHeight) / 2;

        // --- COLORS ---
        int bgColor = 0xC0000000;       // Sleek dark-translucent backing
        int labelColor = 0xFFAAAAAA;    // Soft gray for labels
        int valueColor = 0xFFFFFFFF;    // Clean bright white for stats
        int borderColor = 0xFFBBBBBB;  // Polished silver-iron border

        // 1. Draw Translucent Background
        context.fill(xLeft, yTop, xRight, yTop + boxHeight, bgColor);

        // 2. Draw Labels and Values
        for (int i = 0; i < labels.length; i++) {
            int y = yTop + paddingV + i * rowHeight;
            context.text(font, labels[i], xLeft + paddingH, y, labelColor, false);
            context.text(font, values[i], xRight - paddingH - font.width(values[i]), y, valueColor, false);
        }

        // 3. Draw Polished Outer borderline
        context.fill(xLeft, yTop, xRight, yTop + 1, borderColor);
        context.fill(xLeft, yTop + boxHeight - 1, xRight, yTop + boxHeight, borderColor);
        context.fill(xLeft, yTop, xLeft + 1, yTop + boxHeight, borderColor);
        context.fill(xRight - 1, yTop, xRight, yTop + boxHeight, borderColor);
    };
}