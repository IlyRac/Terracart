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
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;

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

		ModelLayerRegistry.registerModelLayer(
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

		HudElement terracartHud = (context, _) -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.player == null || !(mc.player.getVehicle() instanceof TerracartEntity cart)) return;

			Font font = mc.font;
			// Note: In your class, these are context.guiWidth() and context.guiHeight()
			int screenW = context.guiWidth();
			int screenH = context.guiHeight();

			int boxWidth = 95;
			int boxHeight = 45;
			int xLeft = screenW - boxWidth;
			int yCenter = screenH / 2;
			int yTop = yCenter - (boxHeight / 2);

			// 1. Draw Background
			context.fill(xLeft, yTop, screenW, yTop + boxHeight, 0x40000000);

			int labelX = xLeft + 5;
			int valueX = screenW - 5;

			// Row 1: Speed (Using context.text instead of drawString)
			String speedVal = String.format("%.1f", cart.getSpeedBps()) + " b/s";
			context.text(font, "Speed:", labelX, yCenter - 15, 0xFFAAAAAA);
			context.text(font, speedVal, valueX - font.width(speedVal), yCenter - 15, 0xFFFFFFFF);

			// Row 2: Fuel
			String fuelVal = Math.round(cart.getFuelPercent() * 100.0f) + "%";
			context.text(font, "Fuel:", labelX, yCenter - 3, 0xFFAAAAAA);
			context.text(font, fuelVal, valueX - font.width(fuelVal), yCenter - 3, 0xFFFFFFFF);

			// Row 3: Health
			String healthVal = Math.round(cart.getHealthPercent() * 100.0f) + "%";
			context.text(font, "Condition:", labelX, yCenter + 9, 0xFFAAAAAA);
			context.text(font, healthVal, valueX - font.width(healthVal), yCenter + 9, 0xFFFFFFFF);
		};

		// Register the element.
		// We attach it AFTER the Boss Bar so it renders in the main HUD layer.
		HudElementRegistry.attachElementAfter(
				VanillaHudElements.BOSS_BAR,
				Identifier.fromNamespaceAndPath("terracart", "stats_display"),
				terracartHud
		);
	}
}