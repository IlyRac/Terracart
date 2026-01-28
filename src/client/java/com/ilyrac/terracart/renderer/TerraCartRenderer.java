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

    private static final String[] COLOR_NAMES = {
            "white","orange","magenta","light_blue","yellow","lime","pink","gray",
            "light_gray","cyan","purple","blue","brown","green","red","black"
    };
    private static final Identifier[] TEXTURES = new Identifier[16];
    static {
        for (int i = 0; i < COLOR_NAMES.length; i++) {
            TEXTURES[i] = Identifier.fromNamespaceAndPath(
                    TerraCart.MOD_ID, "textures/entity/"+ COLOR_NAMES[i] +"_terracart.png");
        }
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
//        state.texture = TEXTURE;

        int color = entity.getCartColor(); // -1 .. 15
        if (color >= 0 && color < TEXTURES.length) {
            state.texture = TEXTURES[color];
        } else {
            state.texture = TEXTURE; // fallback default uncolored texture
        }

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