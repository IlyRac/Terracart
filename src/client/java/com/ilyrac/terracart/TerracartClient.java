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
			if (mc.player == null || mc.options.hideGui) return;

			if (mc.player.getVehicle() instanceof TerracartEntity cart) {
				Font font = mc.font;
				int screenW = mc.getWindow().getGuiScaledWidth();
				int screenH = mc.getWindow().getGuiScaledHeight();

				// 1. DIMENSIONS & ANCHORS
				int boxWidth = 95;  // Slightly wider to fit the columns
				int boxHeight = 45;

				int xLeft = screenW - boxWidth;
				int yCenter = screenH / 2;
				int yTop = yCenter - (boxHeight / 2);

				// 2. DRAW BACKGROUND (Ultra-transparent 0x40 alpha)
				gui.fill(xLeft, yTop, screenW, yTop + boxHeight, 0x40000000);

				// 3. DRAW DATA (Aligned in two columns)
				int labelX = xLeft + 5;      // Labels start 5px from left of box
				int valueX = screenW - 5;     // Values end 5px from right of box

				// Row 1: Speed
				String speedVal = String.format("%.1f", cart.getSpeedBps());
				gui.drawString(font, "Speed:", labelX, yCenter - 15, 0xFFAAAAAA, true); // Gray label
				gui.drawString(font, speedVal + " b/s", valueX - font.width(speedVal + " b/s"), yCenter - 15, 0xFFFFFFFF, true);

				// Row 2: Fuel
				String fuelVal = Math.round(cart.getFuelPercent() * 100.0f) + "%";
				gui.drawString(font, "Fuel:", labelX, yCenter - 3, 0xFFAAAAAA, true);
				gui.drawString(font, fuelVal, valueX - font.width(fuelVal), yCenter - 3, 0xFFFFFFFF, true);

				// Row 3: Health
				String healthVal = Math.round(cart.getHealthPercent() * 100.0f) + "%";
				gui.drawString(font, "Condition:", labelX, yCenter + 9, 0xFFAAAAAA, true);
				gui.drawString(font, healthVal, valueX - font.width(healthVal), yCenter + 9, 0xFFFFFFFF, true);
			}
		});
	}
}