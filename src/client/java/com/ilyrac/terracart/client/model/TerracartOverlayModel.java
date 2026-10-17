package com.ilyrac.terracart.client.model;

import com.ilyrac.terracart.client.renderer.state.TerracartRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jspecify.annotations.NonNull;

public class TerracartOverlayModel extends EntityModel<TerracartRenderState> {

    private final ModelPart root;

    public TerracartOverlayModel(ModelPart root) {
        super(root);
        this.root = root;
    }

    @Override
    public void setupAnim(@NonNull TerracartRenderState state) {
        this.root.resetPose();
    }
}
