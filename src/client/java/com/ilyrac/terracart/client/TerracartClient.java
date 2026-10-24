package com.ilyrac.terracart.client;

import com.ilyrac.terracart.Terracart;
import com.ilyrac.terracart.client.hud.TerracartHudRenderer;
import com.ilyrac.terracart.entity.ModEntities;
import com.ilyrac.terracart.entity.TerracartEntity;
import com.ilyrac.terracart.client.model.TerracartModel;
import com.ilyrac.terracart.network.TerracartInputPayload;
import com.ilyrac.terracart.client.renderer.TerracartRenderer;
import com.ilyrac.terracart.client.sound.TerracartSoundController;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public class TerracartClient implements ClientModInitializer {

	private static float lastSentForward = 0.0f;
	private static float lastSentStrafe = 0.0f;
	private static int inputHeartbeat = 0;

	@Override
	public void onInitializeClient() {
		// --- 1. REGISTER RENDERERS & MODELS ---
		EntityRendererRegistry.register(ModEntities.STANDARD_TERRACART, TerracartRenderer::new);
		EntityRendererRegistry.register(ModEntities.SWIFT_TERRACART, TerracartRenderer::new);
		EntityRendererRegistry.register(ModEntities.STURDY_TERRACART, TerracartRenderer::new);
		EntityRendererRegistry.register(ModEntities.PRIME_TERRACART, TerracartRenderer::new);

		// Base Model Layers
		ModelLayerRegistry.registerModelLayer(TerracartModel.STANDARD_LAYER, TerracartModel::createStandardBaseLayer);
		ModelLayerRegistry.registerModelLayer(TerracartModel.SWIFT_LAYER, TerracartModel::createSwiftBaseLayer);
		ModelLayerRegistry.registerModelLayer(TerracartModel.STURDY_LAYER, TerracartModel::createSturdyBaseLayer);
		ModelLayerRegistry.registerModelLayer(TerracartModel.PRIME_LAYER, TerracartModel::createPrimeBaseLayer);

		// Overlay Model Layers (Panels)
		ModelLayerRegistry.registerModelLayer(TerracartModel.STANDARD_OVERLAY_LAYER, TerracartModel::createStandardOverlayLayer);
		ModelLayerRegistry.registerModelLayer(TerracartModel.SWIFT_OVERLAY_LAYER, TerracartModel::createSwiftOverlayLayer);
		ModelLayerRegistry.registerModelLayer(TerracartModel.STURDY_OVERLAY_LAYER, TerracartModel::createSturdyOverlayLayer);
		ModelLayerRegistry.registerModelLayer(TerracartModel.PRIME_OVERLAY_LAYER, TerracartModel::createPrimeOverlayLayer);

		// --- 2. REGISTER HUD ELEMENTS ---
		HudElementRegistry.attachElementAfter(
				VanillaHudElements.BOSS_BAR,
				Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "stats_display"),
				TerracartHudRenderer.STATS_DISPLAY
		);

		// --- 3. COMBINED CLIENT TICK SYSTEMS ---
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.level == null || client.player == null) return;

			// Handle Input Packet with smart throttling (only send when inputs change or on 20-tick heartbeat)
			if (client.player.getVehicle() instanceof TerracartEntity) {
				float fwd = client.player.zza;
				float str = -client.player.xxa;
				inputHeartbeat++;

				if (fwd != lastSentForward || str != lastSentStrafe || inputHeartbeat >= 20) {
					ClientPlayNetworking.send(new TerracartInputPayload(fwd, str));
					lastSentForward = fwd;
					lastSentStrafe = str;
					inputHeartbeat = 0;
				}
			} else {
				lastSentForward = 0.0f;
				lastSentStrafe = 0.0f;
				inputHeartbeat = 0;
			}

			// Handle Client-side Entity Audio
			client.level.entitiesForRendering().forEach(entity -> {
				if (entity instanceof TerracartEntity cart) {
					TerracartSoundController.tick(cart);
				}
			});
		});
	}
}
