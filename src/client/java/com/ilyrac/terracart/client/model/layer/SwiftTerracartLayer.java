package com.ilyrac.terracart.client.model.layer;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SwiftTerracartLayer {

	public static LayerDefinition createBaseLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Terracart = partdefinition.addOrReplaceChild("Terracart", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition Wheels = Terracart.addOrReplaceChild("Wheels", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition FLWheel = Wheels.addOrReplaceChild("FLWheel", CubeListBuilder.create().texOffs(37, 53).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(64, 87).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(88, 17).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(41, 97).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(50, 97).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.5F, 3.0F, -10.0F));

		PartDefinition FRWheel = Wheels.addOrReplaceChild("FRWheel", CubeListBuilder.create().texOffs(60, 53).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(79, 89).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(45, 91).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(59, 98).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(99, 78).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.5F, 3.0F, -10.0F));

		PartDefinition BLWheel = Wheels.addOrReplaceChild("BLWheel", CubeListBuilder.create().texOffs(0, 61).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(92, 0).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(92, 6).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(11, 100).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(20, 100).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.5F, 3.0F, 10.0F));

		PartDefinition BRWheel = Wheels.addOrReplaceChild("BRWheel", CubeListBuilder.create().texOffs(23, 70).addBox(-1.5F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(92, 23).addBox(-1.5F, 4.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(92, 41).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(29, 100).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 100).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.5F, 3.0F, 10.0F));

		PartDefinition Structure = Terracart.addOrReplaceChild("Structure", CubeListBuilder.create(), PartPose.offset(7.0F, -5.0F, 8.0F));

		PartDefinition Base = Structure.addOrReplaceChild("Base", CubeListBuilder.create().texOffs(49, 41).addBox(-9.0F, 5.0F, -19.0F, 18.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(106, 29).addBox(6.0F, 3.0F, -19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(107, 22).addBox(-5.0F, 3.0F, -19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(107, 36).addBox(1.0F, 3.0F, -19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(107, 40).addBox(-2.0F, 3.0F, -19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(107, 44).addBox(4.0F, 3.0F, -19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(107, 48).addBox(-7.0F, 3.0F, -19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(83, 65).addBox(-6.0F, 2.0F, -19.0F, 12.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(49, 47).addBox(-9.0F, 5.0F, 16.0F, 18.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(107, 73).addBox(6.0F, 3.0F, 18.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(107, 94).addBox(-7.0F, 3.0F, 18.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(37, 108).addBox(-7.0F, 1.0F, 19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(42, 108).addBox(6.0F, 1.0F, 19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(47, 108).addBox(4.0F, 1.0F, 19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(52, 108).addBox(-5.0F, 1.0F, 19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(73, 108).addBox(-2.0F, 1.0F, 19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(108, 77).addBox(1.0F, 1.0F, 19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(108, 81).addBox(1.0F, 3.0F, 18.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(108, 85).addBox(-2.0F, 3.0F, 18.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(-5.0F, 3.0F, 18.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(109, 10).addBox(4.0F, 3.0F, 18.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(84, 33).addBox(-6.0F, 0.0F, 19.0F, 12.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, 5.0F, -16.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 15).addBox(-6.0F, 5.0F, 4.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 30).addBox(-9.0F, 5.0F, -4.0F, 18.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(86, 95).addBox(6.0F, 2.0F, 9.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(38, 103).addBox(-7.0F, 2.0F, -11.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(45, 103).addBox(-7.0F, 2.0F, 9.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(7, 106).addBox(6.0F, -3.0F, -5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(12, 106).addBox(-7.0F, -3.0F, -5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(17, 106).addBox(-7.0F, -3.0F, 4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(52, 103).addBox(-7.0F, -4.0F, 5.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(103, 68).addBox(6.0F, -4.0F, 5.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(22, 106).addBox(6.0F, -3.0F, 4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(46, 70).addBox(-7.0F, -3.0F, -4.0F, 1.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(65, 70).addBox(6.0F, -3.0F, -4.0F, 1.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(59, 104).addBox(6.0F, 2.0F, -11.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(49, 0).addBox(-6.0F, -2.0F, -13.0F, 12.0F, 7.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(83, 53).addBox(-6.0F, -3.0F, -15.0F, 12.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 5.0F, -8.0F));

		PartDefinition Hood = Structure.addOrReplaceChild("Hood", CubeListBuilder.create().texOffs(0, 41).addBox(-14.0F, -1.0F, 0.0F, 14.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(84, 36).addBox(-11.0F, -1.0F, -20.0F, 8.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(92, 12).addBox(-10.0F, -1.0F, -17.0F, 6.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(84, 68).addBox(-9.0F, -2.0F, -23.0F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(92, 47).addBox(-9.0F, -3.0F, -24.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(23, 61).addBox(-5.0F, -2.0F, -24.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(94, 94).addBox(-11.0F, -2.0F, -24.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(75, 95).addBox(-12.0F, -1.0F, -24.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 97).addBox(-3.0F, -1.0F, -24.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(15, 87).addBox(-12.0F, 0.0F, -24.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(30, 87).addBox(-3.0F, 0.0F, -24.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(99, 84).addBox(-2.0F, 1.0F, -24.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(95, 100).addBox(-13.0F, 1.0F, -24.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(57, 109).addBox(-13.0F, 1.0F, -24.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(107, 26).addBox(-2.0F, 2.0F, -24.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(84, 75).addBox(-12.0F, 1.0F, -23.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(45, 87).addBox(-11.0F, -1.0F, -23.0F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Spoiler = Structure.addOrReplaceChild("Spoiler", CubeListBuilder.create().texOffs(0, 103).addBox(-3.0F, -2.0F, 12.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(23, 67).addBox(-3.0F, -1.0F, 11.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 67).addBox(-13.0F, -1.0F, 11.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(103, 16).addBox(-13.0F, -2.0F, 12.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 100).addBox(-13.0F, -4.0F, 13.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(66, 105).addBox(-3.0F, -4.0F, 13.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(53, 28).addBox(-15.0F, -5.0F, 12.0F, 16.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(60, 93).addBox(0.0F, -6.0F, 12.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(94, 89).addBox(-18.0F, -6.0F, 12.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Window = Terracart.addOrReplaceChild("Window", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Frame = Window.addOrReplaceChild("Frame", CubeListBuilder.create().texOffs(27, 106).addBox(-1.0F, -18.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(32, 106).addBox(-16.0F, -21.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 106).addBox(-16.0F, -24.0F, 3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(87, 106).addBox(-16.0F, -18.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(107, 0).addBox(-1.0F, -24.0F, 3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(107, 5).addBox(-1.0F, -21.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(53, 38).addBox(-15.0F, -25.0F, 3.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 53).addBox(-16.0F, -15.0F, 1.0F, 16.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 8.0F, -8.0F));

		PartDefinition Glass = Window.addOrReplaceChild("Glass", CubeListBuilder.create().texOffs(83, 57).addBox(-14.0F, -2.0F, 2.0F, 14.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(53, 33).addBox(-14.0F, -5.0F, 2.0F, 14.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(83, 61).addBox(-14.0F, -8.0F, 3.0F, 14.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -8.0F, -8.0F));

		PartDefinition steering = Terracart.addOrReplaceChild("steering", CubeListBuilder.create().texOffs(99, 29).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(92, 29).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(15, 95).addBox(-2.0F, -4.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(28, 95).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(95, 105).addBox(-2.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(68, 98).addBox(-3.0F, -3.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(75, 101).addBox(2.0F, -3.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(102, 105).addBox(1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -3.0F));

		PartDefinition Saddle = Terracart.addOrReplaceChild("Saddle", CubeListBuilder.create().texOffs(49, 17).addBox(-2.0F, 4.0F, -3.0F, 12.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(84, 78).addBox(-2.0F, 0.0F, -2.0F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 86).addBox(9.0F, 0.0F, -2.0F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 78).addBox(-1.0F, -2.0F, 3.0F, 10.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(109, 89).addBox(-2.0F, -2.0F, 3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(109, 105).addBox(9.0F, -2.0F, 3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -2.0F, 3.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	public static LayerDefinition createOverlayLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Terracart = partdefinition.addOrReplaceChild("Terracart", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));
		PartDefinition Structure = Terracart.addOrReplaceChild("Structure", CubeListBuilder.create(), PartPose.offset(7.0F, -5.0F, 8.0F));

		PartDefinition Body = Structure.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(37, 41).addBox(-8.0F, 0.0F, -3.75F, 16.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(45, 0).addBox(-8.0F, 2.0F, -4.75F, 16.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(37, 49).addBox(-6.0F, -1.0F, -1.75F, 12.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(41, 37).addBox(-8.0F, -1.0F, -2.75F, 16.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 60).addBox(-6.0F, 1.0F, -2.75F, 12.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(29, 59).addBox(7.0F, -3.0F, 9.25F, 1.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(29, 50).addBox(7.0F, -4.0F, 8.25F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(67, 60).addBox(-8.0F, -4.0F, 8.25F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(67, 67).addBox(-8.0F, -1.0F, 17.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(18, 71).addBox(7.0F, -1.0F, 17.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 59).addBox(-8.0F, -3.0F, 9.25F, 1.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 67).addBox(-8.0F, -4.0F, 17.25F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(9, 67).addBox(7.0F, -4.0F, 17.25F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(18, 67).addBox(6.0F, -2.0F, -1.75F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(67, 56).addBox(-8.0F, -2.0F, -1.75F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 9).addBox(-8.0F, -3.0F, 0.25F, 16.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(41, 32).addBox(-8.0F, -4.0F, 3.25F, 16.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(37, 24).addBox(-6.0F, -2.0F, 0.25F, 12.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 4.0F, -21.25F));

		Body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(66, 49).addBox(-6.0F, 4.5F, -14.5F, 12.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(45, 12).addBox(-8.0F, 2.5F, -14.5F, 16.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(45, 6).addBox(-8.0F, 3.5F, -17.5F, 16.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-8.0F, -0.5F, -18.5F, 16.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 41).addBox(-8.0F, 3.5F, -16.5F, 16.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 50).addBox(-6.0F, 2.5F, -13.5F, 12.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(37, 17).addBox(-8.0F, -0.5F, -14.5F, 16.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 17).addBox(-6.0F, 1.5F, -11.5F, 12.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.0F, -0.5F, -11.5F, 16.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.5F, 14.75F, 0.0F, 3.1416F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
}