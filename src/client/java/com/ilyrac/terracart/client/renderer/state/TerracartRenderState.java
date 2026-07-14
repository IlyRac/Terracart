package com.ilyrac.terracart.client.renderer.state;

import com.ilyrac.terracart.Terracart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

public class TerracartRenderState extends EntityRenderState {

    private static final String[] COLOR_NAMES = {
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    };

    private static final Identifier DEFAULT_TEXTURE = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "textures/entity/terracart.png");
    private static final Identifier[] TEXTURES = new Identifier[16];

    static {
        for (int i = 0; i < COLOR_NAMES.length; i++) {
            TEXTURES[i] = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "textures/entity/" + COLOR_NAMES[i] + "_terracart.png");
        }
    }

    public float yaw;
    public float wheelRotation;
    public float steeringRotation;
    public float frontWheelYaw;
    public Identifier texture = DEFAULT_TEXTURE;

    public void setColor(int colorIndex) {
        this.texture = (colorIndex >= 0 && colorIndex < TEXTURES.length)
                ? TEXTURES[colorIndex]
                : DEFAULT_TEXTURE;
    }
}