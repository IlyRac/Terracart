package com.ilyrac.terracart.client.model.layer;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SturdyTerracartLayer {

	public static LayerDefinition createBaseLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Terracart = partdefinition.addOrReplaceChild("Terracart", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition Wheels = Terracart.addOrReplaceChild("Wheels", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition FLWheel = Wheels.addOrReplaceChild("FLWheel", CubeListBuilder.create().texOffs(81, 84).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(110, 99).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 111).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(46, 101).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(37, 114).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.5F, 3.0F, -10.0F));

		PartDefinition FRWheel = Wheels.addOrReplaceChild("FRWheel", CubeListBuilder.create().texOffs(92, 12).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(57, 107).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(68, 109).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(115, 21).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(97, 116).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.5F, 3.0F, -10.0F));

		PartDefinition BLWheel = Wheels.addOrReplaceChild("BLWheel", CubeListBuilder.create().texOffs(76, 3).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(113, 33).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(113, 39).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(106, 116).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 117).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.5F, 3.0F, 10.0F));

		PartDefinition BRWheel = Wheels.addOrReplaceChild("BRWheel", CubeListBuilder.create().texOffs(0, 94).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(113, 45).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(15, 114).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(46, 117).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(55, 117).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.5F, 3.0F, 10.0F));

		PartDefinition Structure = Terracart.addOrReplaceChild("Structure", CubeListBuilder.create(), PartPose.offset(7.0F, -5.0F, 8.0F));

		PartDefinition Base = Structure.addOrReplaceChild("Base", CubeListBuilder.create().texOffs(49, 23).addBox(-9.0F, 1.0F, -19.0F, 18.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(58, 97).addBox(-7.0F, -4.0F, -19.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(107, 107).addBox(5.0F, -4.0F, -19.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(74, 56).addBox(-7.0F, -5.0F, 18.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 30).addBox(-4.0F, -2.0F, 18.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(105, 35).addBox(-1.0F, -2.0F, 18.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(96, 56).addBox(-5.0F, -4.0F, 18.0F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(91, 36).addBox(2.0F, -2.0F, 18.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 117).addBox(5.0F, -5.0F, 18.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(36, 102).addBox(2.0F, -3.0F, -19.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(26, 105).addBox(-1.0F, -3.0F, -19.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(15, 96).addBox(-4.0F, -3.0F, -19.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(96, 60).addBox(-5.0F, -5.0F, -19.0F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(47, 29).addBox(-9.0F, 1.0F, 16.0F, 18.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, 0.0F, -16.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 43).addBox(-6.0F, -2.0F, -15.0F, 12.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(53, 35).addBox(-6.0F, -3.0F, -13.0F, 12.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 16).addBox(-6.0F, 0.0F, 4.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(45, 43).addBox(-6.0F, -2.0F, 5.0F, 12.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 56).addBox(-6.0F, -3.0F, 7.0F, 12.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-9.0F, 1.0F, -4.0F, 18.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(54, 64).addBox(8.0F, -5.0F, -6.0F, 1.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(89, 64).addBox(-9.0F, -5.0F, 6.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(54, 79).addBox(-9.0F, -5.0F, -6.0F, 1.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(89, 73).addBox(8.0F, -5.0F, 6.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(113, 27).addBox(8.0F, -2.0F, -3.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(117, 69).addBox(8.0F, -2.0F, 1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(117, 76).addBox(-9.0F, -2.0F, -3.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 94).addBox(-9.0F, -3.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(84, 35).addBox(-9.0F, -2.0F, 1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(90, 29).addBox(8.0F, -3.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(90, 52).addBox(-7.0F, 3.0F, -11.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(85, 43).addBox(-7.0F, 2.0F, -11.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(98, 35).addBox(6.0F, 2.0F, -11.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(77, 0).addBox(6.0F, 2.0F, 9.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(97, 112).addBox(-7.0F, 2.0F, 9.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 64).addBox(-7.0F, -5.0F, -5.0F, 1.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(79, 106).addBox(-6.0F, -5.0F, 6.0F, 12.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(56, 94).addBox(-7.0F, -2.0F, -5.0F, 1.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(27, 64).addBox(6.0F, -5.0F, -5.0F, 1.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(106, 106).addBox(6.0F, 0.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(23, 101).addBox(6.0F, -2.0F, -5.0F, 1.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(46, 107).addBox(-7.0F, 0.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(91, 0).addBox(-7.0F, 3.0F, 9.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 5.0F, -8.0F));

		PartDefinition Hood = Structure.addOrReplaceChild("Hood", CubeListBuilder.create().texOffs(39, 11).addBox(-12.0F, -2.0F, -25.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(37, 0).addBox(-14.0F, -2.0F, 0.0F, 14.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Window = Terracart.addOrReplaceChild("Window", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Frame = Window.addOrReplaceChild("Frame", CubeListBuilder.create().texOffs(120, 27).addBox(-1.0F, -18.0F, 1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(71, 117).addBox(-2.0F, -18.0F, 19.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(80, 115).addBox(-16.0F, -18.0F, 19.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(40, 105).addBox(-1.0F, -15.0F, 19.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(9, 117).addBox(0.0F, -15.0F, 19.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(119, 56).addBox(-16.0F, -15.0F, 19.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(15, 86).addBox(-17.0F, -15.0F, 19.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(28, 86).addBox(-16.0F, -21.0F, 2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(106, 64).addBox(-16.0F, -24.0F, 3.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(106, 70).addBox(-16.0F, -18.0F, 1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(106, 76).addBox(-1.0F, -24.0F, 3.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(99, 39).addBox(-1.0F, -24.0F, 17.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(105, 95).addBox(-16.0F, -24.0F, 17.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(69, 100).addBox(-15.0F, -23.0F, 18.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(83, 111).addBox(-2.0F, -23.0F, 18.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(69, 94).addBox(-5.0F, -23.0F, 18.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(117, 105).addBox(-14.0F, -23.0F, 18.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(117, 108).addBox(-1.0F, -21.0F, 2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(97, 29).addBox(-1.0F, -21.0F, 18.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(37, 120).addBox(-2.0F, -21.0F, 18.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(115, 116).addBox(-16.0F, -21.0F, 18.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(91, 8).addBox(-15.0F, -25.0F, 4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(96, 86).addBox(-15.0F, -24.0F, 3.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(96, 89).addBox(-15.0F, -25.0F, 17.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(97, 83).addBox(-15.0F, -24.0F, 18.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 80).addBox(-15.0F, -25.0F, 5.0F, 1.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(27, 80).addBox(-2.0F, -25.0F, 5.0F, 1.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(69, 56).addBox(-16.0F, -24.0F, 5.0F, 1.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(69, 70).addBox(-1.0F, -24.0F, 5.0F, 1.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(89, 111).addBox(0.0F, -24.0F, 10.0F, 1.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(118, 63).addBox(-1.0F, -25.0F, 10.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(91, 4).addBox(-15.0F, -26.0F, 10.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(120, 12).addBox(-16.0F, -25.0F, 10.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(75, 86).addBox(-16.0F, -15.0F, 10.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(82, 86).addBox(-1.0F, -15.0F, 10.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(30, 114).addBox(-17.0F, -24.0F, 10.0F, 1.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(37, 56).addBox(-16.0F, -15.0F, 1.0F, 16.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 8.0F, -8.0F));

		PartDefinition Glass = Window.addOrReplaceChild("Glass", CubeListBuilder.create().texOffs(96, 122).addBox(-14.0F, -2.0F, 2.0F, 14.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(80, 47).addBox(-14.0F, -5.0F, 2.0F, 14.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 123).addBox(-14.0F, -7.0F, 3.0F, 14.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -8.0F, -8.0F));

		PartDefinition steering = Terracart.addOrReplaceChild("steering", CubeListBuilder.create().texOffs(15, 111).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(106, 40).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(107, 11).addBox(-2.0F, -4.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(115, 16).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(75, 4).addBox(-2.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(47, 108).addBox(-3.0F, -3.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(74, 97).addBox(2.0F, -3.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(92, 42).addBox(1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -3.0F));

		PartDefinition Saddle = Terracart.addOrReplaceChild("Saddle", CubeListBuilder.create().texOffs(79, 101).addBox(-2.0F, 2.0F, -2.0F, 12.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(23, 94).addBox(-2.0F, 0.0F, -2.0F, 12.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -2.0F, 3.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	public static LayerDefinition createOverlayLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Terracart = partdefinition.addOrReplaceChild("Terracart", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));
		PartDefinition Structure = Terracart.addOrReplaceChild("Structure", CubeListBuilder.create(), PartPose.offset(7.0F, -5.0F, 8.0F));

		PartDefinition Body = Structure.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 44).addBox(-8.0F, -4.0F, -4.75F, 16.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(37, 44).addBox(-8.0F, -4.0F, -2.75F, 16.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 26).addBox(-8.0F, -4.0F, 0.25F, 16.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 13).addBox(-6.0F, -5.0F, -4.75F, 12.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(47, 13).addBox(-8.0F, -1.0F, -2.75F, 16.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(37, 51).addBox(7.0F, -5.0F, 8.25F, 1.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(55, 0).addBox(7.0F, 1.0F, 9.25F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(62, 51).addBox(-8.0F, 1.0F, 9.25F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 53).addBox(-8.0F, -5.0F, 8.25F, 1.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(23, 53).addBox(-8.0F, -5.0F, 18.25F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(30, 53).addBox(7.0F, -5.0F, 18.25F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 4.0F, -21.25F));

		Body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(47, 17).addBox(-8.0F, 3.0F, -16.0F, 16.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(45, 33).addBox(-8.0F, 0.0F, -18.0F, 16.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(45, 26).addBox(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.0F, -1.0F, -18.0F, 16.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(0, 35).addBox(-8.0F, 0.0F, -13.0F, 16.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 13.25F, 0.0F, 3.1416F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
}