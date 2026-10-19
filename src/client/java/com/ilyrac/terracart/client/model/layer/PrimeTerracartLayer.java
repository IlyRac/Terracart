package com.ilyrac.terracart.client.model.layer;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class PrimeTerracartLayer {

	public static LayerDefinition createBaseLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Terracart = partdefinition.addOrReplaceChild("Terracart", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition Wheels = Terracart.addOrReplaceChild("Wheels", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition FLWheel = Wheels.addOrReplaceChild("FLWheel", CubeListBuilder.create().texOffs(64, 56).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(92, 0).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(92, 6).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(27, 64).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(60, 99).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.5F, 3.0F, -10.0F));

		PartDefinition FRWheel = Wheels.addOrReplaceChild("FRWheel", CubeListBuilder.create().texOffs(27, 71).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(52, 93).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(19, 94).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(69, 99).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(19, 100).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.5F, 3.0F, -10.0F));

		PartDefinition BLWheel = Wheels.addOrReplaceChild("BLWheel", CubeListBuilder.create().texOffs(50, 73).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(34, 94).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(96, 73).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(28, 100).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(37, 100).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.5F, 3.0F, 10.0F));

		PartDefinition BRWheel = Wheels.addOrReplaceChild("BRWheel", CubeListBuilder.create().texOffs(73, 73).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(96, 79).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(98, 85).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(78, 100).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(87, 100).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.5F, 3.0F, 10.0F));

		PartDefinition Structure = Terracart.addOrReplaceChild("Structure", CubeListBuilder.create(), PartPose.offset(7.0F, -5.0F, 8.0F));

		PartDefinition Base = Structure.addOrReplaceChild("Base", CubeListBuilder.create().texOffs(49, 0).addBox(-9.0F, 1.0F, -19.0F, 18.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(7, 108).addBox(-7.0F, -2.0F, -19.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(66, 110).addBox(-6.0F, -3.0F, -19.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(79, 110).addBox(5.0F, -3.0F, -19.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(105, 53).addBox(-5.0F, -4.0F, -19.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(93, 108).addBox(3.0F, -4.0F, -19.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 53).addBox(-3.0F, -5.0F, -19.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(100, 108).addBox(6.0F, -2.0F, -19.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(19, 106).addBox(3.0F, -3.0F, -19.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(24, 106).addBox(-4.0F, -3.0F, -19.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(14, 102).addBox(-2.0F, -4.0F, -19.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(103, 101).addBox(1.0F, -4.0F, -19.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(49, 6).addBox(-9.0F, 1.0F, 16.0F, 18.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(29, 106).addBox(6.0F, -3.0F, 18.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(34, 106).addBox(3.0F, -3.0F, 18.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(39, 106).addBox(-4.0F, -3.0F, 18.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(7, 102).addBox(-1.0F, -3.0F, 18.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(52, 90).addBox(-6.0F, -4.0F, 18.0F, 12.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(74, 106).addBox(-7.0F, -3.0F, 18.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, 0.0F, -16.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 43).addBox(-6.0F, -2.0F, -15.0F, 12.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(49, 12).addBox(-6.0F, -3.0F, -13.0F, 12.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 16).addBox(-6.0F, 0.0F, 4.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(45, 43).addBox(-6.0F, -2.0F, 5.0F, 12.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(49, 20).addBox(-6.0F, -3.0F, 7.0F, 12.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-9.0F, 1.0F, -4.0F, 18.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(110, 56).addBox(-9.0F, -1.0F, 2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(110, 60).addBox(-9.0F, -1.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(46, 105).addBox(-9.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(67, 93).addBox(-9.0F, -2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(61, 110).addBox(8.0F, -1.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(53, 105).addBox(8.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(110, 64).addBox(8.0F, -1.0F, 2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(49, 99).addBox(8.0F, -2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(86, 25).addBox(-7.0F, 3.0F, -11.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(79, 106).addBox(-7.0F, 2.0F, -11.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(86, 106).addBox(6.0F, 2.0F, -11.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(107, 0).addBox(6.0F, 2.0F, 9.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(107, 4).addBox(-7.0F, 2.0F, 9.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(37, 56).addBox(-7.0F, -4.0F, -5.0F, 1.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(90, 43).addBox(-6.0F, -4.0F, 6.0F, 12.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 79).addBox(-7.0F, -2.0F, -5.0F, 1.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(79, 90).addBox(-7.0F, 0.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 64).addBox(6.0F, -4.0F, -5.0F, 1.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(87, 56).addBox(6.0F, -2.0F, -5.0F, 1.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 92).addBox(6.0F, 0.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(86, 29).addBox(-7.0F, 3.0F, 9.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 5.0F, -8.0F));

		PartDefinition Hood = Structure.addOrReplaceChild("Hood", CubeListBuilder.create().texOffs(86, 19).addBox(-13.0F, -2.0F, 0.0F, 12.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(90, 47).addBox(-11.0F, -2.0F, 4.0F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(53, 28).addBox(-10.0F, -2.0F, -25.0F, 6.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Window = Terracart.addOrReplaceChild("Window", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Frame = Window.addOrReplaceChild("Frame", CubeListBuilder.create().texOffs(108, 101).addBox(-1.0F, -18.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(105, 108).addBox(-16.0F, -21.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(-16.0F, -24.0F, 3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(12, 109).addBox(-16.0F, -18.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(44, 110).addBox(-1.0F, -24.0F, 3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(49, 110).addBox(-1.0F, -21.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(53, 40).addBox(-15.0F, -25.0F, 3.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 56).addBox(-16.0F, -15.0F, 1.0F, 16.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 8.0F, -8.0F));

		PartDefinition Glass = Window.addOrReplaceChild("Glass", CubeListBuilder.create().texOffs(87, 69).addBox(-14.0F, -2.0F, 2.0F, 14.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(86, 33).addBox(-14.0F, -5.0F, 2.0F, 14.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(23, 90).addBox(-14.0F, -8.0F, 3.0F, 14.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -8.0F, -8.0F));

		PartDefinition steering = Terracart.addOrReplaceChild("steering", CubeListBuilder.create().texOffs(54, 110).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(107, 8).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(98, 91).addBox(-2.0F, -4.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(98, 96).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(60, 105).addBox(-2.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(96, 101).addBox(-3.0F, -3.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 102).addBox(2.0F, -3.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(67, 105).addBox(1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -3.0F));

		PartDefinition Saddle = Terracart.addOrReplaceChild("Saddle", CubeListBuilder.create().texOffs(86, 38).addBox(-2.0F, 2.0F, -2.0F, 12.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(86, 12).addBox(-2.0F, 0.0F, -2.0F, 12.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -2.0F, 3.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	public static LayerDefinition createOverlayLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Terracart = partdefinition.addOrReplaceChild("Terracart", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));
		PartDefinition Structure = Terracart.addOrReplaceChild("Structure", CubeListBuilder.create(), PartPose.offset(7.0F, -5.0F, 8.0F));

		PartDefinition Body = Structure.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(45, 22).addBox(-8.0F, -2.0F, -4.75F, 16.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(58, 54).addBox(-7.0F, -3.0F, -4.75F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(54, 70).addBox(-7.0F, -4.0F, 4.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(61, 70).addBox(6.0F, -4.0F, 4.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(68, 70).addBox(4.0F, -5.0F, 4.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(72, 50).addBox(-5.0F, -5.0F, 4.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(37, 45).addBox(-8.0F, -2.0F, -2.75F, 16.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(58, 58).addBox(-7.0F, -3.0F, -2.75F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 13).addBox(-8.0F, -3.0F, -0.75F, 16.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, -4.0F, -4.75F, 12.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(0, 31).addBox(-4.0F, -5.0F, -4.75F, 8.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(47, 16).addBox(-8.0F, -1.0F, -2.75F, 16.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(47, 0).addBox(7.0F, -4.0F, 8.25F, 1.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 68).addBox(7.0F, 1.0F, 9.25F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(70, 0).addBox(-8.0F, 1.0F, 9.25F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 52).addBox(-8.0F, -4.0F, 8.25F, 1.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(40, 70).addBox(-8.0F, -4.0F, 18.25F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(47, 70).addBox(7.0F, -4.0F, 18.25F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 4.0F, -21.25F));

		Body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(37, 50).addBox(-8.0F, 2.5F, -16.0F, 16.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(33, 70).addBox(6.0F, -3.5F, -18.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(61, 74).addBox(5.0F, -6.5F, -19.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(54, 74).addBox(-6.0F, -6.5F, -19.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(52, 66).addBox(-6.0F, -7.5F, -19.0F, 12.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(26, 70).addBox(6.0F, -6.5F, -19.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(19, 70).addBox(-7.0F, -6.5F, -19.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(70, 10).addBox(-7.0F, -3.5F, -18.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(56, 62).addBox(-7.0F, -0.5F, -18.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(23, 57).addBox(-7.0F, 0.5F, -18.0F, 15.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 44).addBox(-8.0F, 0.5F, -18.0F, 16.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(23, 54).addBox(-8.0F, -0.5F, -14.0F, 16.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(23, 60).addBox(-7.0F, -0.5F, -16.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(39, 39).addBox(-8.0F, 0.5F, -16.0F, 16.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(23, 64).addBox(-5.0F, -1.5F, -17.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(39, 31).addBox(-7.0F, -1.5F, -13.0F, 14.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 22).addBox(-8.0F, -0.5F, -13.0F, 16.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.5F, 13.25F, 0.0F, 3.1416F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
}