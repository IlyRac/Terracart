package com.ilyrac.terracart;

import com.ilyrac.terracart.entity.ModEntities;
import com.ilyrac.terracart.entity.TerracartEntity;
import com.ilyrac.terracart.model.TerracartModel;
import com.ilyrac.terracart.renderer.TerracartRenderer;
import com.ilyrac.terracart.sound.TerracartSoundController;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class TerracartClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
        //noinspection deprecation
        EntityRendererRegistry.register(
				ModEntities.TERRACART,
				TerracartRenderer::new
		);

		EntityModelLayerRegistry.registerModelLayer(
				TerracartModel.LAYER,
				TerracartModel::createBodyLayer
		);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.level == null) return;
			client.level.entitiesForRendering().forEach(entity -> {
				if (entity instanceof TerracartEntity cart) {
					TerracartSoundController.tick(cart);
				}
			});
		});

        //noinspection deprecation
        HudRenderCallback.EVENT.register((gui, tickDelta) -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.player == null) return;

			// Respect vanilla options:
			// - hideGui is toggled by F1
			// - renderDebug is toggled by F3 (optional; include if you want to hide HUD while debug is shown)
			if (mc.options.hideGui) return;

			// show while riding
			if (mc.player.getVehicle() instanceof TerracartEntity cart) {
				float fuelPercent = cart.getFuelPercent();
				float speed = cart.getSpeedBps();

				Font font = mc.font;
				int screenW = mc.getWindow().getGuiScaledWidth();
				int screenH = mc.getWindow().getGuiScaledHeight();

				String fuelText = "Fuel: " + Math.round(fuelPercent * 100.0f) + "%";
				int x = Math.toIntExact(Math.round((screenW - font.width(fuelText)) / 1.25));
				int y = screenH - 50;
				gui.drawString(font, fuelText, x, y, 0xFFFFFFFF, false);

				String speedText = String.format("Speed: %.1f m/s", speed);
				int sx = Math.toIntExact(Math.round((screenW - font.width(speedText)) / 1.25));
				int sy = screenH - 60;
				gui.drawString(font, speedText, sx, sy, 0xFFFFFFFF, false);
			}
		});
	}
}