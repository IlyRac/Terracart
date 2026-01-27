package com.ilyrac.terracart.renderer;

import com.ilyrac.terracart.TerraCart;
import com.ilyrac.terracart.entity.TerraCartEntity;
import com.ilyrac.terracart.model.TerraCartModel;
import com.ilyrac.terracart.renderer.state.TerraCartRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

public class TerraCartRenderer extends EntityRenderer<TerraCartEntity, TerraCartRenderState> {

    private final TerraCartModel model;

    public TerraCartRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.9F;

        this.model = new TerraCartModel(
                context.bakeLayer(TerraCartModel.LAYER)
        );
    }

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            TerraCart.MOD_ID,
            "textures/entity/terracart.png"
    );

    @Override
    public TerraCartRenderState createRenderState() {
        return new TerraCartRenderState();
    }

    @Override
    public void extractRenderState(
            TerraCartEntity entity,
            TerraCartRenderState state,
            float partialTick
    ) {
        super.extractRenderState(entity, state, partialTick);
        state.yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.texture = TEXTURE;

        float wheelPrev = entity.getPrevWheelRotation();
        float wheelCurr = entity.getWheelRotation();
        state.wheelRotation = Mth.lerp(partialTick, wheelPrev, wheelCurr);
    }

    @Override
    public void submit(
            TerraCartRenderState state,
            PoseStack poseStack,
            @NonNull SubmitNodeCollector collector,
            @NonNull CameraRenderState camera
    ) {
        poseStack.pushPose();

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
}