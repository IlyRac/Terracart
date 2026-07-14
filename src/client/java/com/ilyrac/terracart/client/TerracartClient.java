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

	@Override
	public void onInitializeClient() {
		// --- 1. REGISTER RENDERERS & MODELS ---
		//noinspection deprecation
		EntityRendererRegistry.register(
				ModEntities.TERRACART,
				TerracartRenderer::new
		);

		ModelLayerRegistry.registerModelLayer(
				TerracartModel.LAYER,
				TerracartModel::createBodyLayer
		);

		// --- 2. REGISTER HUD ELEMENTS ---
		HudElementRegistry.attachElementAfter(
				VanillaHudElements.BOSS_BAR,
				Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "stats_display"),
				TerracartHudRenderer.STATS_DISPLAY
		);

		// --- 3. COMBINED CLIENT TICK SYSTEMS ---
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.level == null || client.player == null) return;

			// Handle Input Packet
			if (client.player.getVehicle() instanceof TerracartEntity) {
				ClientPlayNetworking.send(
						new TerracartInputPayload(
								client.player.zza,
								-client.player.xxa
						)
				);
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