package com.ilyrac.terracart;

import com.ilyrac.terracart.entity.ModEntities;
import com.ilyrac.terracart.entity.TerracartEntity;
import com.ilyrac.terracart.model.TerracartModel;
import com.ilyrac.terracart.network.TerracartInputPayload;
import com.ilyrac.terracart.renderer.TerracartRenderer;
import com.ilyrac.terracart.sound.TerracartSoundController;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class TerracartClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;
			if (!(client.player.getVehicle() instanceof com.ilyrac.terracart.entity.TerracartEntity)) return;

			ClientPlayNetworking.send(
					new TerracartInputPayload(
							client.player.zza,
							-client.player.xxa
					)
			);
		});

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
			if (mc.options.hideGui) return;

			if (mc.player.getVehicle() instanceof TerracartEntity cart) {
				float fuelPercent = cart.getFuelPercent();
				float speed = cart.getSpeedBps();
				float healthPercent = cart.getHealthPercent();

				Font font = mc.font;
				int screenW = mc.getWindow().getGuiScaledWidth();
				int screenH = mc.getWindow().getGuiScaledHeight();

				// 1. Speed
				String speedText = String.format("Speed: %.1f b/s", speed);
				int sx = Math.toIntExact(Math.round((screenW - font.width(speedText)) / 1.35));
				int sy = screenH - 40;
				gui.drawString(font, speedText, sx, sy, 0xFFFFFFFF, false);

				// 2. Fuel
				String fuelText = "Fuel: " + Math.round(fuelPercent * 100.0f) + " %";
				int x = Math.toIntExact(Math.round((screenW - font.width(fuelText)) / 1.35));
				int y = screenH - 30;
				gui.drawString(font, fuelText, x, y, 0xFFFFFFFF, false);

				// 3. NEW: Health
				String healthText = "Health: " + Math.round(healthPercent * 100.0f) + " %";
				int hx = Math.toIntExact(Math.round((screenW - font.width(healthText)) / 1.35));
				int hy = screenH - 20;
				gui.drawString(font, healthText, hx, hy, 0xFFFFFFFF, false);
			}
		});
	}
}