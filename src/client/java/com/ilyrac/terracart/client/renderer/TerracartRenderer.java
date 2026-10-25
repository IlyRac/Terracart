package com.ilyrac.terracart.client.renderer;

import com.ilyrac.terracart.client.model.TerracartModel;
import com.ilyrac.terracart.client.model.TerracartOverlayModel;
import com.ilyrac.terracart.client.renderer.state.TerracartRenderState;
import com.ilyrac.terracart.entity.TerracartEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.NonNull;

public class TerracartRenderer extends EntityRenderer<TerracartEntity, TerracartRenderState> {

    private final TerracartModel standardModel;
    private final TerracartOverlayModel standardOverlayModel;

    private final TerracartModel swiftModel;
    private final TerracartOverlayModel swiftOverlayModel;

    private final TerracartModel sturdyModel;
    private final TerracartOverlayModel sturdyOverlayModel;

    private final TerracartModel primeModel;
    private final TerracartOverlayModel primeOverlayModel;

    public TerracartRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.9F;

        this.standardModel = new TerracartModel(context.bakeLayer(TerracartModel.STANDARD_LAYER));
        this.standardOverlayModel = new TerracartOverlayModel(context.bakeLayer(TerracartModel.STANDARD_OVERLAY_LAYER));

        this.swiftModel = new TerracartModel(context.bakeLayer(TerracartModel.SWIFT_LAYER));
        this.swiftOverlayModel = new TerracartOverlayModel(context.bakeLayer(TerracartModel.SWIFT_OVERLAY_LAYER));

        this.sturdyModel = new TerracartModel(context.bakeLayer(TerracartModel.STURDY_LAYER));
        this.sturdyOverlayModel = new TerracartOverlayModel(context.bakeLayer(TerracartModel.STURDY_OVERLAY_LAYER));

        this.primeModel = new TerracartModel(context.bakeLayer(TerracartModel.PRIME_LAYER));
        this.primeOverlayModel = new TerracartOverlayModel(context.bakeLayer(TerracartModel.PRIME_OVERLAY_LAYER));
    }

    @Override
    public @NonNull TerracartRenderState createRenderState() {
        return new TerracartRenderState();
    }

    @Override
    public void extractRenderState(@NonNull TerracartEntity entity, @NonNull TerracartRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);

        // 1. Motion, Tier, and Color State Extraction
        state.yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.wheelRotation = Mth.lerp(partialTick, entity.getPrevWheelRotation(), entity.getWheelRotation());
        state.setTier(entity.getTier());
        state.setColor(entity.getCartColor());

        // 2. Drive Steering Input Extraction
        float steeringDegrees = 0.0F;
        float groundWheelDegrees = 0.0F;

        float strafe = entity.getSyncedStrafe();

        if (strafe < 0.0F) {
            steeringDegrees = -15.0F;
            groundWheelDegrees = -10.75F;
        } else if (strafe > 0.0F) {
            steeringDegrees = 15.0F;
            groundWheelDegrees = 10.75F;
        }

        // Convert raw angles to standard Radians
        state.steeringRotation = steeringDegrees * Mth.DEG_TO_RAD;
        state.frontWheelYaw = groundWheelDegrees * Mth.DEG_TO_RAD;

        // 3. Conditional Name Tag Extraction (Respects distance & aiming)
        Minecraft mc = Minecraft.getInstance();
        boolean shouldShow = false;

        if (entity.hasCustomName() && mc.player != null) {
            double distanceSq = entity.distanceToSqr(mc.player);

            // Distance threshold (16 blocks -> 16^2 = 256)
            if (distanceSq <= 256.0) {
                if (entity.isCustomNameVisible()) {
                    shouldShow = true;
                } else if (mc.hitResult instanceof EntityHitResult entityHitResult) {
                    shouldShow = entityHitResult.getEntity() == entity;
                }
            }
        }

        if (shouldShow) {
            state.nameTag = entity.getCustomName();
            state.nameTagAttachment = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getYRot(partialTick));
        } else {
            state.nameTag = null;
        }
    }

    @Override
    public void submit(@NonNull TerracartRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);
        poseStack.pushPose();

        // Sequential transformations
        poseStack.scale(1.3F, 1.3F, 1.3F);
        poseStack.translate(0.0F, 1.5F, 0.0F);
        poseStack.rotate(Axis.XP.rotationDegrees(180.0F));
        poseStack.rotate(Axis.YP.rotationDegrees(state.yaw));

        TerracartModel activeBaseModel = switch (state.tier) {
            case SWIFT -> this.swiftModel;
            case STURDY -> this.sturdyModel;
            case PRIME -> this.primeModel;
            default -> this.standardModel;
        };

        TerracartOverlayModel activeOverlayModel = switch (state.tier) {
            case SWIFT -> this.swiftOverlayModel;
            case STURDY -> this.sturdyOverlayModel;
            case PRIME -> this.primeOverlayModel;
            default -> this.standardOverlayModel;
        };

        // Pass 1: Base Chassis Layer (Untinted factory metal & glass)
        collector.submitModel(
                activeBaseModel,
                state,
                poseStack,
                activeBaseModel.renderType(state.baseTexture),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0xFFFFFFFF,
                null,
                0
        );

        // Pass 2: Body Panels Overlay Layer (Tinted by dye color or factory default)
        int overlayTint = TerracartPaint.getOverlayColor(state.color);
        collector.submitModel(
                activeOverlayModel,
                state,
                poseStack,
                activeOverlayModel.renderType(state.overlayTexture),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                overlayTint,
                null,
                0
        );


        poseStack.popPose();
    }

    @Override
    protected Component getNameTag(TerracartEntity entity) {
        return entity.getCustomName();
    }

    @Override
    protected boolean shouldShowName(@NonNull TerracartEntity entity, double distanceToCameraSq) {
        return false;
    }
}