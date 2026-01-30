package com.ilyrac.terracart.renderer.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

public class TerracartRenderState extends EntityRenderState {
    public float yaw;
    public Identifier texture;
    public float wheelRotation = 0.0F;

    public TerracartRenderState() {
        super();
    }
}
