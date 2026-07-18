package com.ilyrac.terracart.client.model;

import com.ilyrac.terracart.Terracart;
import com.ilyrac.terracart.client.renderer.state.TerracartRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;

public class TerracartModel extends EntityModel<TerracartRenderState> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "terracart"), "main");

    private final ModelPart root;
    private final ModelPart frontLeftWheel;
    private final ModelPart frontRightWheel;
    private final ModelPart backWheels;
    private final ModelPart steeringWheel;

    public TerracartModel(ModelPart root) {
        super(root);
        this.root = root;

        ModelPart wheels = root.getChild("Wheels");
        this.frontLeftWheel = wheels.getChild("Front_Left");
        this.frontRightWheel = wheels.getChild("Front_Right");
        this.backWheels = wheels.getChild("Back");
        this.steeringWheel = root.getChild("steering_wheel");
    }

    /* ===================== GEOMETRY ===================== */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Wheels Root Group
        PartDefinition Wheels = partdefinition.addOrReplaceChild("Wheels", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));

        // FRONT LEFT WHEEL
        Wheels.addOrReplaceChild("Front_Left", CubeListBuilder.create()
                        .texOffs(210, 223).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(233, 221).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(233, 215).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(248, 221).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(248, 227).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(8.5F, 3.0F, -10.0F));

        // FRONT RIGHT WHEEL
        Wheels.addOrReplaceChild("Front_Right", CubeListBuilder.create()
                        .texOffs(210, 189).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(233, 239).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(233, 245).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(248, 215).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(248, 209).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-8.5F, 3.0F, -10.0F));

        // BACK WHEELS
        Wheels.addOrReplaceChild("Back", CubeListBuilder.create()
                        .texOffs(210, 240).addBox(7.0F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(233, 251).addBox(7.0F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(233, 209).addBox(7.0F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(248, 233).addBox(7.0F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(248, 239).addBox(7.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(210, 206).addBox(-10.0F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(233, 227).addBox(-10.0F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(233, 233).addBox(-10.0F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(248, 245).addBox(-10.0F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(248, 251).addBox(-10.0F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 3.0F, 10.0F));

        // Structure Hierarchy Setup
        PartDefinition Structure = partdefinition.addOrReplaceChild("Structure", CubeListBuilder.create(), PartPose.offset(7.0F, 11.0F, 8.0F));

        Structure.addOrReplaceChild("Base", CubeListBuilder.create()
                .texOffs(45, 58).addBox(-9.0F, 1.0F, -19.0F, 18.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(58, 18).addBox(6.0F, -4.0F, 18.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 6).addBox(3.0F, -3.0F, 18.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 25).addBox(-1.0F, -2.0F, 18.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 12).addBox(-4.0F, -3.0F, 18.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(63, 6).addBox(-7.0F, -4.0F, 18.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 0).addBox(-4.0F, -3.0F, -19.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(63, 13).addBox(-7.0F, -4.0F, -19.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(63, 0).addBox(3.0F, -3.0F, -19.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(63, 20).addBox(6.0F, -4.0F, -19.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 30).addBox(-1.0F, -2.0F, -19.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(45, 65).addBox(-9.0F, 1.0F, 16.0F, 18.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-6.0F, 0.0F, -16.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 43).addBox(-6.0F, -2.0F, -15.0F, 12.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 72).addBox(-6.0F, -3.0F, -13.0F, 12.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 16).addBox(-6.0F, 0.0F, 4.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 57).addBox(-6.0F, -2.0F, 5.0F, 12.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(38, 72).addBox(-6.0F, -3.0F, 7.0F, 12.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 32).addBox(-9.0F, 1.0F, -4.0F, 18.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(48, 50).addBox(-7.0F, 3.0F, -11.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(68, 4).addBox(-7.0F, 2.0F, -11.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(68, 0).addBox(6.0F, 2.0F, -11.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(68, 12).addBox(6.0F, 2.0F, 9.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(68, 8).addBox(-7.0F, 2.0F, 9.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 54).addBox(-7.0F, 3.0F, 9.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 5.0F, -8.0F));

        PartDefinition Body = Structure.addOrReplaceChild("Body", CubeListBuilder.create()
                .texOffs(116, 18).addBox(-8.0F, -4.0F, -4.75F, 16.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(116, 46).addBox(-8.0F, -4.0F, -2.75F, 16.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(119, 98).addBox(-8.0F, -6.0F, 6.25F, 16.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(116, 0).addBox(-8.0F, -4.0F, 0.25F, 16.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(116, 53).addBox(-8.0F, -1.0F, -2.75F, 16.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(117, 73).addBox(6.0F, -4.0F, 8.25F, 2.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(138, 62).addBox(6.0F, 1.0F, 9.25F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(117, 62).addBox(-8.0F, 1.0F, 9.25F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(143, 73).addBox(-8.0F, -4.0F, 8.25F, 2.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(127, 91).addBox(-8.0F, -4.0F, 18.25F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(118, 91).addBox(6.0F, -4.0F, 18.25F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 4.0F, -21.25F));

        Body.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                .texOffs(116, 57).addBox(-8.0F, 1.0F, -3.0F, 16.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(116, 28).addBox(-8.0F, -2.0F, -5.0F, 16.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(116, 38).addBox(-8.0F, -2.0F, -3.0F, 16.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(116, 9).addBox(-8.0F, -2.0F, 0.0F, 16.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 26.25F, 0.0F, 3.1416F, 0.0F));

        Structure.addOrReplaceChild("Hood", CubeListBuilder.create()
                .texOffs(210, 0).addBox(-14.0F, -1.0F, 0.0F, 14.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(224, 25).addBox(-9.0F, -2.0F, -27.0F, 4.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(228, 11).addBox(-5.0F, -1.0F, -27.0F, 2.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(210, 37).addBox(-5.0F, 0.0F, -27.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(199, 11).addBox(-11.0F, -1.0F, -27.0F, 2.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(217, 37).addBox(-11.0F, 0.0F, -27.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(199, 36).addBox(-9.0F, 0.0F, -27.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        // Window Setup
        PartDefinition Window = partdefinition.addOrReplaceChild("Window", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));

        Window.addOrReplaceChild("Frame", CubeListBuilder.create()
                .texOffs(0, 252).addBox(-1.0F, -18.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(5, 252).addBox(-16.0F, -21.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 252).addBox(-16.0F, -24.0F, 3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(15, 252).addBox(-16.0F, -18.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 252).addBox(-1.0F, -24.0F, 3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(25, 252).addBox(-1.0F, -21.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 239).addBox(-15.0F, -25.0F, 3.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 8.0F, -8.0F));

        Window.addOrReplaceChild("Glass", CubeListBuilder.create()
                .texOffs(30, 242).addBox(-14.0F, -2.0F, 1.0F, 14.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 247).addBox(-14.0F, -5.0F, 2.0F, 14.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 252).addBox(-14.0F, -8.0F, 3.0F, 14.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -8.0F, -8.0F));

        // steering setup
        partdefinition.addOrReplaceChild("steering_holder", CubeListBuilder.create()
                .texOffs(151, 254).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(152, 250).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, -3.0F));

        partdefinition.addOrReplaceChild("steering_wheel", CubeListBuilder.create()
                .texOffs(138, 240).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(138, 252).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(145, 247).addBox(-2.0F, -2.0F, 0.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(138, 245).addBox(-3.0F, -3.0F, 0.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(138, 245).addBox(2.0F, -3.0F, 0.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(145, 247).addBox(1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, -4.0F));

        // saddle setup
        partdefinition.addOrReplaceChild("Saddle", CubeListBuilder.create()
                .texOffs(88, 252).addBox(-2.0F, 2.0F, -2.0F, 12.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(87, 245).addBox(-2.0F, 0.0F, -2.0F, 12.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 14.0F, 3.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    /* ===================== ANIMATION ===================== */
    @Override
    public void setupAnim(TerracartRenderState state) {
        this.root.resetPose();

        float rotation = -state.wheelRotation;

        // 1. Back Wheels (Spin)
        this.backWheels.xRot = rotation;

        // 2. Front (Left + Right) Wheels (Turn + Spin)
        this.frontLeftWheel.xRot = rotation;
        this.frontLeftWheel.yRot = state.frontWheelYaw;

        this.frontRightWheel.xRot = rotation;
        this.frontRightWheel.yRot = state.frontWheelYaw;

        // 3. Steering Wheel (Rotation)
        this.steeringWheel.zRot = -state.steeringRotation;
    }
}