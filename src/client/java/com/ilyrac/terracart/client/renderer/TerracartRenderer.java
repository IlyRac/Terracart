package com.ilyrac.terracart.client.renderer;

import com.ilyrac.terracart.client.model.TerracartModel;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.NonNull;

public class TerracartRenderer extends EntityRenderer<TerracartEntity, TerracartRenderState> {

    private final TerracartModel model;

    public TerracartRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.9F;
        this.model = new TerracartModel(context.bakeLayer(TerracartModel.LAYER));
    }

    @Override
    public @NonNull TerracartRenderState createRenderState() {
        return new TerracartRenderState();
    }

    @Override
    public void extractRenderState(@NonNull TerracartEntity entity, @NonNull TerracartRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);

        // 1. Motion and Color State Extraction
        state.yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.wheelRotation = Mth.lerp(partialTick, entity.getPrevWheelRotation(), entity.getWheelRotation());
        state.setColor(entity.getCartColor());

        // 2. Drive Steering Input Extraction
        float steeringDegrees = 0.0F;
        float groundWheelDegrees = 0.0F;

        if (entity.getControllingPassenger() instanceof LivingEntity rider) {
            if (rider.xxa > 0.0F) {
                steeringDegrees = -15.0F;
                groundWheelDegrees = -10.75F;
            } else if (rider.xxa < 0.0F) {
                steeringDegrees = 15.0F;
                groundWheelDegrees = 10.75F;
            }
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
                    // Only show if the player's crosshair is pointing at this specific cart
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
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yaw));

        collector.submitModel(
                model,
                state,
                poseStack,
                model.renderType(state.texture),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0,
                null
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