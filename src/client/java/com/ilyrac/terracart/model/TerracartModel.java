package com.ilyrac.terracart.model;

import com.ilyrac.terracart.renderer.state.TerracartRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;

public class TerracartModel extends EntityModel<TerracartRenderState> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath("terracart", "terracart"), "main");

    private final ModelPart root;
    private final ModelPart frontWheels;
    private final ModelPart backWheels;

    public TerracartModel(ModelPart root) {
        super(root);

        this.root = root;

        ModelPart terracart = root.getChild("Terracart");
        ModelPart wheels = terracart.getChild("Wheels");
        this.frontWheels = wheels.getChild("Front");
        this.backWheels = wheels.getChild("Back");
    }

    /* ===================== GEOMETRY ===================== */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Terracart = partdefinition.addOrReplaceChild("Terracart", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 14.363F, -5.0019F, 0.0F, 3.1416F, 0.0F));

        Terracart.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, 3.1111F, -19.1944F, 16.0F, 2.0F, 26.0F, new CubeDeformation(0.0F))
                .texOffs(72, 41).addBox(-8.0F, -4.8889F, -19.1944F, 2.0F, 8.0F, 26.0F, new CubeDeformation(0.0F))
                .texOffs(0, 51).addBox(-6.0F, -4.8889F, 0.8056F, 12.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(72, 5).addBox(6.0F, -4.8889F, -19.1944F, 2.0F, 8.0F, 26.0F, new CubeDeformation(0.0F))
                .texOffs(0, 32).addBox(-6.0F, -4.8889F, -19.1944F, 12.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(61, 9).addBox(-6.0F, 1.1111F, -11.1944F, 12.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(61, 0).addBox(-6.0F, -5.8889F, -18.1944F, 12.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(59, 19).addBox(-8.0F, 5.1111F, -1.1944F, 16.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(33, 30).addBox(-8.0F, 5.1111F, -14.1944F, 16.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5259F, 1.1963F));

        Terracart.addOrReplaceChild("Window", CubeListBuilder.create().texOffs(36, 119).addBox(7.0F, -3.7F, -0.8F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(41, 119).addBox(-8.0F, -3.7F, -0.8F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 111).addBox(-7.0F, -4.7F, -0.8F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 114).addBox(-7.0F, 3.3F, -0.8F, 14.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 120).addBox(-7.0F, -3.7F, -0.8F, 14.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.663F, 3.8018F));

        PartDefinition Wheels = Terracart.addOrReplaceChild("Wheels", CubeListBuilder.create(), PartPose.offset(0.0F, 6.137F, -4.9981F));

        Wheels.addOrReplaceChild("Front", CubeListBuilder.create().texOffs(116, 118).addBox(8.0F, -2.5F, -2.5F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(56, 124).addBox(8.0F, 2.5F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(83, 124).addBox(8.0F, -1.5F, 2.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(98, 124).addBox(8.0F, -1.5F, -3.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(65, 124).addBox(8.0F, -3.5F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(103, 118).addBox(-9.0F, -2.5F, -2.5F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(47, 124).addBox(-9.0F, 2.5F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(88, 124).addBox(-9.0F, -1.5F, -3.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(93, 124).addBox(-9.0F, -1.5F, 2.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(74, 124).addBox(-9.0F, -3.5F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 6.5F));

        Wheels.addOrReplaceChild("Back", CubeListBuilder.create().texOffs(103, 106).addBox(-9.0F, -2.5F, -2.5F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(65, 117).addBox(-9.0F, 2.5F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(88, 118).addBox(-9.0F, -1.5F, 2.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(93, 118).addBox(-9.0F, -1.5F, -3.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(74, 117).addBox(-9.0F, -3.5F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(116, 106).addBox(8.0F, -2.5F, -2.5F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(98, 118).addBox(8.0F, -1.5F, 2.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(47, 117).addBox(8.0F, -3.5F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(83, 118).addBox(8.0F, -1.5F, -3.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 117).addBox(8.0F, 2.5F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.5F));

        Terracart.addOrReplaceChild("steering", CubeListBuilder.create().texOffs(71, 101).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(79, 102).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(103, 100).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(116, 100).addBox(-2.0F, 2.0F, -3.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(63, 100).addBox(-2.0F, 0.0F, -3.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(55, 100).addBox(1.0F, 0.0F, -3.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(87, 98).addBox(2.0F, -1.0F, -3.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(95, 98).addBox(-3.0F, -1.0F, -3.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.363F, 2.0019F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    /* ===================== ANIMATION ===================== */
    @Override
    public void setupAnim(TerracartRenderState state) {
        this.root.resetPose();

        float rotation = state.wheelRotation;

        this.frontWheels.xRot = rotation;
        this.backWheels.xRot = rotation;
    }
}