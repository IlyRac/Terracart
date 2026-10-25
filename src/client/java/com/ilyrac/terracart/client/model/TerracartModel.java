package com.ilyrac.terracart.client.model;

import com.ilyrac.terracart.Terracart;
import com.ilyrac.terracart.client.model.layer.PrimeTerracartLayer;
import com.ilyrac.terracart.client.model.layer.StandardTerracartLayer;
import com.ilyrac.terracart.client.model.layer.SturdyTerracartLayer;
import com.ilyrac.terracart.client.model.layer.SwiftTerracartLayer;
import com.ilyrac.terracart.client.renderer.state.TerracartRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.Identifier;

public class TerracartModel extends EntityModel<TerracartRenderState> {

    // --- Base Layer Locations ---
    public static final ModelLayerLocation STANDARD_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "standard_terracart"), "main");
    public static final ModelLayerLocation SWIFT_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "swift_terracart"), "main");
    public static final ModelLayerLocation STURDY_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "sturdy_terracart"), "main");
    public static final ModelLayerLocation PRIME_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "prime_terracart"), "main");

    // --- Overlay Layer Locations ---
    public static final ModelLayerLocation STANDARD_OVERLAY_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "standard_terracart"), "overlay");
    public static final ModelLayerLocation SWIFT_OVERLAY_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "swift_terracart"), "overlay");
    public static final ModelLayerLocation STURDY_OVERLAY_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "sturdy_terracart"), "overlay");
    public static final ModelLayerLocation PRIME_OVERLAY_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "prime_terracart"), "overlay");

    // Compatibility alias
    public static final ModelLayerLocation LAYER = STANDARD_LAYER;

    private final ModelPart root;
    private final ModelPart flWheel;
    private final ModelPart frWheel;
    private final ModelPart blWheel;
    private final ModelPart brWheel;
    private final ModelPart steering;

    public TerracartModel(ModelPart root) {
        super(root);
        this.root = root;

        ModelPart terracart = root.getChild("Terracart");
        ModelPart wheels = terracart.getChild("Wheels");
        this.flWheel = wheels.getChild("FLWheel");
        this.frWheel = wheels.getChild("FRWheel");
        this.blWheel = wheels.getChild("BLWheel");
        this.brWheel = wheels.getChild("BRWheel");
        this.steering = terracart.getChild("steering");
    }

    // --- Base Layer Builders ---
    public static LayerDefinition createStandardBaseLayer() {
        return StandardTerracartLayer.createBaseLayer();
    }

    public static LayerDefinition createSwiftBaseLayer() {
        return SwiftTerracartLayer.createBaseLayer();
    }

    public static LayerDefinition createSturdyBaseLayer() {
        return SturdyTerracartLayer.createBaseLayer();
    }

    public static LayerDefinition createPrimeBaseLayer() {
        return PrimeTerracartLayer.createBaseLayer();
    }

    // --- Overlay Layer Builders ---
    public static LayerDefinition createStandardOverlayLayer() {
        return StandardTerracartLayer.createOverlayLayer();
    }

    public static LayerDefinition createSwiftOverlayLayer() {
        return SwiftTerracartLayer.createOverlayLayer();
    }

    public static LayerDefinition createSturdyOverlayLayer() {
        return SturdyTerracartLayer.createOverlayLayer();
    }

    public static LayerDefinition createPrimeOverlayLayer() {
        return PrimeTerracartLayer.createOverlayLayer();
    }

    // Legacy fallback
    public static LayerDefinition createBodyLayer() {
        return createStandardBaseLayer();
    }

    @Override
    public void setupAnim(TerracartRenderState state) {
        this.root.resetPose();

        float rotation = -state.wheelRotation;

        // 1. Back Wheels (Spin)
        this.blWheel.xRot = rotation;
        this.brWheel.xRot = rotation;

        // 2. Front Wheels (Turn + Spin)
        this.flWheel.xRot = rotation;
        this.flWheel.yRot = state.frontWheelYaw;

        this.frWheel.xRot = rotation;
        this.frWheel.yRot = state.frontWheelYaw;

        // 3. Steering Wheel (Rotation around Z axis)
        this.steering.zRot = -state.steeringRotation;
    }
}