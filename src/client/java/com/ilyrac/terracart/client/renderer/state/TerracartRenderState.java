package com.ilyrac.terracart.client.renderer.state;

import com.ilyrac.terracart.Terracart;
import com.ilyrac.terracart.tier.TerracartTier;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

public class TerracartRenderState extends EntityRenderState {

    private static final Identifier STANDARD_BASE = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "textures/entity/standard_terracart_base.png");
    private static final Identifier STANDARD_OVERLAY = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "textures/entity/standard_terracart_overlay.png");

    private static final Identifier SWIFT_BASE = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "textures/entity/swift_terracart_base.png");
    private static final Identifier SWIFT_OVERLAY = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "textures/entity/swift_terracart_overlay.png");

    private static final Identifier STURDY_BASE = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "textures/entity/sturdy_terracart_base.png");
    private static final Identifier STURDY_OVERLAY = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "textures/entity/sturdy_terracart_overlay.png");

    private static final Identifier PRIME_BASE = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "textures/entity/prime_terracart_base.png");
    private static final Identifier PRIME_OVERLAY = Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "textures/entity/prime_terracart_overlay.png");

    public float yaw;
    public float wheelRotation;
    public float steeringRotation;
    public float frontWheelYaw;
    public int color = -1;
    public TerracartTier tier = TerracartTier.STANDARD;

    public Identifier baseTexture = STANDARD_BASE;
    public Identifier overlayTexture = STANDARD_OVERLAY;

    public void setTier(TerracartTier tier) {
        this.tier = tier != null ? tier : TerracartTier.STANDARD;
        switch (this.tier) {
            case SWIFT -> {
                this.baseTexture = SWIFT_BASE;
                this.overlayTexture = SWIFT_OVERLAY;
            }
            case STURDY -> {
                this.baseTexture = STURDY_BASE;
                this.overlayTexture = STURDY_OVERLAY;
            }
            case PRIME -> {
                this.baseTexture = PRIME_BASE;
                this.overlayTexture = PRIME_OVERLAY;
            }
            default -> {
                this.baseTexture = STANDARD_BASE;
                this.overlayTexture = STANDARD_OVERLAY;
            }
        }
    }

    public void setColor(int colorIndex) {
        this.color = colorIndex;
    }
}