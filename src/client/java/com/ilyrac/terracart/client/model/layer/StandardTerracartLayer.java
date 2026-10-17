package com.ilyrac.terracart.client.model.layer;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class StandardTerracartLayer {

	public static LayerDefinition createBaseLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Terracart = partdefinition.addOrReplaceChild("Terracart", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition Wheels = Terracart.addOrReplaceChild("Wheels", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition FLWheel = Wheels.addOrReplaceChild("FLWheel", CubeListBuilder.create().texOffs(54, 70).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(94, 20).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(96, 31).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(37, 55).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(27, 63).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.5F, 3.0F, -10.0F));

		PartDefinition FRWheel = Wheels.addOrReplaceChild("FRWheel", CubeListBuilder.create().texOffs(77, 62).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(69, 96).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(84, 96).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(100, 71).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(100, 77).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.5F, 3.0F, -10.0F));

		PartDefinition BLWheel = Wheels.addOrReplaceChild("BLWheel", CubeListBuilder.create().texOffs(0, 78).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(19, 98).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(99, 96).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(100, 83).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(100, 89).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.5F, 3.0F, 10.0F));

		PartDefinition BRWheel = Wheels.addOrReplaceChild("BRWheel", CubeListBuilder.create().texOffs(77, 79).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(34, 100).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(49, 100).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(64, 102).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(73, 102).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.5F, 3.0F, 10.0F));

		PartDefinition Structure = Terracart.addOrReplaceChild("Structure", CubeListBuilder.create(), PartPose.offset(7.0F, -5.0F, 8.0F));

		PartDefinition Base = Structure.addOrReplaceChild("Base", CubeListBuilder.create().texOffs(49, 26).addBox(-9.0F, 1.0F, -19.0F, 18.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(53, 32).addBox(-9.0F, 1.0F, 16.0F, 18.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, 0.0F, -16.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(49, 0).addBox(-6.0F, -2.0F, -15.0F, 12.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(49, 54).addBox(-6.0F, -3.0F, -13.0F, 12.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 16).addBox(-6.0F, 0.0F, 4.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(49, 13).addBox(-6.0F, -2.0F, 5.0F, 12.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 55).addBox(-6.0F, -3.0F, 7.0F, 12.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-9.0F, 1.0F, -4.0F, 18.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(53, 38).addBox(-7.0F, 3.0F, -11.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(103, 102).addBox(-7.0F, 2.0F, -11.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(19, 104).addBox(6.0F, 2.0F, -11.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(26, 104).addBox(6.0F, 2.0F, 9.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 105).addBox(-7.0F, 2.0F, 9.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 63).addBox(6.0F, -4.0F, -5.0F, 1.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 95).addBox(-7.0F, 0.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(23, 85).addBox(-7.0F, -2.0F, -5.0F, 1.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(27, 70).addBox(-7.0F, -4.0F, -5.0F, 1.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(96, 42).addBox(6.0F, 0.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(46, 87).addBox(6.0F, -2.0F, -5.0F, 1.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(94, 16).addBox(-6.0F, -4.0F, 6.0F, 12.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 38).addBox(-7.0F, 3.0F, 9.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 5.0F, -8.0F));

		PartDefinition Hood = Structure.addOrReplaceChild("Hood", CubeListBuilder.create().texOffs(49, 43).addBox(-14.0F, -1.0F, 0.0F, 14.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 43).addBox(-14.0F, -1.0F, -25.0F, 14.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Window = Terracart.addOrReplaceChild("Window", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Frame = Window.addOrReplaceChild("Frame", CubeListBuilder.create().texOffs(37, 62).addBox(-16.0F, -15.0F, 1.0F, 16.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(14, 105).addBox(-1.0F, -18.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(33, 106).addBox(-16.0F, -21.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(38, 106).addBox(-16.0F, -24.0F, 3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(43, 106).addBox(-16.0F, -18.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 106).addBox(-1.0F, -24.0F, 3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(53, 106).addBox(-1.0F, -21.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(94, 5).addBox(-15.0F, -25.0F, 3.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 8.0F, -8.0F));

		PartDefinition Glass = Window.addOrReplaceChild("Glass", CubeListBuilder.create().texOffs(94, 8).addBox(-14.0F, -2.0F, 2.0F, 14.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(92, 26).addBox(-14.0F, -5.0F, 2.0F, 14.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(94, 12).addBox(-14.0F, -8.0F, 3.0F, 14.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -8.0F, -8.0F));

		PartDefinition steering = Terracart.addOrReplaceChild("steering", CubeListBuilder.create().texOffs(103, 106).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(7, 105).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(100, 61).addBox(-2.0F, -4.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(100, 66).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(89, 102).addBox(-2.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(69, 87).addBox(-3.0F, -3.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(82, 102).addBox(2.0F, -3.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(96, 102).addBox(1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -3.0F));

		PartDefinition Saddle = Terracart.addOrReplaceChild("Saddle", CubeListBuilder.create().texOffs(94, 0).addBox(-2.0F, 2.0F, -2.0F, 12.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(86, 54).addBox(-2.0F, 0.0F, -2.0F, 12.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -2.0F, 3.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	public static LayerDefinition createOverlayLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Terracart = partdefinition.addOrReplaceChild("Terracart", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));
		PartDefinition Structure = Terracart.addOrReplaceChild("Structure", CubeListBuilder.create(), PartPose.offset(7.0F, -5.0F, 8.0F));

		PartDefinition Body = Structure.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 18).addBox(-8.0F, -4.0F, -4.75F, 16.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 27).addBox(-8.0F, -4.0F, -2.75F, 16.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.0F, -4.0F, 0.25F, 16.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(39, 27).addBox(-8.0F, -1.0F, -2.75F, 16.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(39, 35).addBox(7.0F, -4.0F, 8.25F, 1.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(45, 0).addBox(7.0F, 1.0F, 9.25F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(23, 51).addBox(-8.0F, 1.0F, 9.25F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 41).addBox(-8.0F, -4.0F, 8.25F, 1.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(23, 41).addBox(-8.0F, -4.0F, 18.25F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(30, 41).addBox(7.0F, -4.0F, 18.25F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 4.0F, -21.25F));

		Body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(39, 31).addBox(-8.0F, 2.5F, -16.0F, 16.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(37, 18).addBox(-8.0F, -0.5F, -18.0F, 16.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 34).addBox(-8.0F, -0.5F, -16.0F, 16.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 9).addBox(-8.0F, -0.5F, -13.0F, 16.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.5F, 13.25F, 0.0F, 3.1416F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
}