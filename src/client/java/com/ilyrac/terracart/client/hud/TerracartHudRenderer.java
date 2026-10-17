package com.ilyrac.terracart.client.hud;

import com.ilyrac.terracart.entity.TerracartEntity;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class TerracartHudRenderer {

    public static final HudElement STATS_DISPLAY = (context, _) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !(mc.player.getVehicle() instanceof TerracartEntity cart)) return;

        Font font = mc.font;
        int screenW = context.guiWidth();
        int screenH = context.guiHeight();

        // --- HUD DATA SETUP ---
        // 1. Data values
        float maxSpeed = cart.getCoreStats().maxSpeed();
        int maxFuelRate = cart.getCoreStats().maxFuel();
        int currentFuelRate = Math.round(cart.getFuelPercent() * (float) maxFuelRate);
        int currentHp = Math.round(cart.getHealth());
        int maxHp = Math.round(cart.getMaxHealth());

        // 2. Components for each column
        Component[] labels = {
                Component.literal("Speed:").withStyle(ChatFormatting.GRAY),
                Component.literal("Fuel:").withStyle(ChatFormatting.GRAY),
                Component.literal("Condition:").withStyle(ChatFormatting.GRAY)
        };

        Component[] currentComps = {
                Component.literal(String.format("%.1f", cart.getSpeedBps())).withStyle(ChatFormatting.WHITE),
                Component.literal(String.valueOf(currentFuelRate)).withStyle(ChatFormatting.WHITE),
                Component.literal(String.valueOf(currentHp)).withStyle(ChatFormatting.WHITE)
        };

        Component[] maxComps = {
                Component.literal("/ " + String.format("%.1f", maxSpeed)).withStyle(ChatFormatting.DARK_GRAY),
                Component.literal("/ " + maxFuelRate).withStyle(ChatFormatting.DARK_GRAY),
                Component.literal("/ " + maxHp).withStyle(ChatFormatting.DARK_GRAY)
        };

        Component[] unitComps = {
                Component.literal("B/S").withStyle(ChatFormatting.DARK_GRAY),
                Component.literal("FU").withStyle(ChatFormatting.DARK_GRAY),
                Component.literal("HP").withStyle(ChatFormatting.DARK_GRAY)
        };

        // --- COLUMN WIDTH CALCULATIONS (Template-based for jitter-free stability) ---
        int maxLabelWidth = 0;
        for (Component label : labels) {
            maxLabelWidth = Math.max(maxLabelWidth, font.width(label));
        }

        int maxCurrentWidth = Math.max(
                font.width("88.8"),
                Math.max(
                        font.width(String.valueOf(Math.max(maxFuelRate, 100))),
                        font.width(String.valueOf(Math.max(maxHp, 100)))
                )
        );

        int maxSlashPartWidth = 0;
        for (Component maxComp : maxComps) {
            maxSlashPartWidth = Math.max(maxSlashPartWidth, font.width(maxComp));
        }

        int maxUnitWidth = 0;
        for (Component unitComp : unitComps) {
            maxUnitWidth = Math.max(maxUnitWidth, font.width(unitComp));
        }

        // --- STYLING & COLUMN GAPS ---
        int paddingH = 5;
        int paddingV = 5;
        int lineSpacing = 10;
        int fontHeight = 8;
        int margin = 6;

        int gapLabelToCurrent = 6;
        int gapCurrentToSlash = 4;
        int gapSlashToUnit = 5;

        int boxWidth = paddingH * 2 + maxLabelWidth + gapLabelToCurrent + maxCurrentWidth + gapCurrentToSlash + maxSlashPartWidth + gapSlashToUnit + maxUnitWidth;
        int boxHeight = paddingV * 2 + (labels.length - 1) * lineSpacing + fontHeight;

        // --- POSITIONING (Vertically Centered, Right Aligned) ---
        int xLeft = screenW - boxWidth - margin;
        int xRight = xLeft + boxWidth;
        int yTop = (screenH - boxHeight) / 2;

        // Column X coordinates
        int colLabelX = xLeft + paddingH;
        int colCurrentRightX = colLabelX + maxLabelWidth + gapLabelToCurrent + maxCurrentWidth;
        int colSlashX = colCurrentRightX + gapCurrentToSlash;
        int colUnitX = colSlashX + maxSlashPartWidth + gapSlashToUnit;

        // --- COLORS ---
        int bgColor = 0xC0000000;       // Sleek dark-translucent backing
        int borderColor = 0x80555555;   // Subtle border

        // 1. Draw Translucent Background
        context.fill(xLeft, yTop, xRight, yTop + boxHeight, bgColor);

        // 2. Draw Tabular Columns with symmetric vertical placement
        for (int i = 0; i < labels.length; i++) {
            int y = yTop + paddingV + i * lineSpacing;

            // Column 1: Label (left-aligned)
            context.text(font, labels[i], colLabelX, y, 0xFFFFFFFF, false);

            // Column 2: Current value (right-aligned)
            int currentX = colCurrentRightX - font.width(currentComps[i]);
            context.text(font, currentComps[i], currentX, y, 0xFFFFFFFF, false);

            // Column 3: Slash & Max value (vertically aligned)
            context.text(font, maxComps[i], colSlashX, y, 0xFFFFFFFF, false);

            // Column 4: Unit in Maj (vertically aligned)
            context.text(font, unitComps[i], colUnitX, y, 0xFFFFFFFF, false);
        }

        // 3. Draw Polished Outer border
        context.fill(xLeft, yTop, xRight, yTop + 1, borderColor);
        context.fill(xLeft, yTop + boxHeight - 1, xRight, yTop + boxHeight, borderColor);
        context.fill(xLeft, yTop, xLeft + 1, yTop + boxHeight, borderColor);
        context.fill(xRight - 1, yTop, xRight, yTop + boxHeight, borderColor);
    };
}