package com.ilyrac.terracart.client.renderer;

public class TerracartPaint {
    public static final int FACTORY_DEFAULT = 0xFF828282;

    // Tint colors for the 16 vanilla dyes (format: 0xAARRGGBB)
    public static final int[] DYE_HEX_COLORS = new int[] {
            0xFFFFFFFF, // 0: White
            0xFFFF6A00, // 1: Orange
            0xFFDF20DF, // 2: Magenta (Vibrant)
            0xFF00E5FF, // 3: Light Blue (Vibrant)
            0xFFFFD500, // 4: Yellow
            0xFF95FF00, // 5: Lime (Vibrant)
            0xFFFF2A85, // 6: Pink (Vibrant)
            0xFF828282, // 7: Gray (Factory default)
            0xFFBFBFBF, // 8: Light Gray
            0xFF00BFA5, // 9: Cyan (Vibrant)
            0xFF8E24AA, // 10: Purple (Vibrant)
            0xFF1565C0, // 11: Blue (Vibrant)
            0xFF8D5B28, // 12: Brown (Vibrant)
            0xFF2E7D32, // 13: Green (Vibrant)
            0xFFDF2020, // 14: Red (Vibrant)
            0xFF1C1C1C  // 15: Black
    };

    public static int getOverlayColor(int colorIndex) {
        if (colorIndex >= 0 && colorIndex < DYE_HEX_COLORS.length) {
            return DYE_HEX_COLORS[colorIndex];
        }
        return FACTORY_DEFAULT;
    }
}
