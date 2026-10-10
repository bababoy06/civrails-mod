package com.civrails.mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.civrails.mod.auth.LinkManager;

public class CivRailsClient implements ClientModInitializer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger("civrails");

    private static final KeyMapping OPEN_SETTINGS_KEY = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                    "key.civrails.open_settings",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_O,
                    KeyMapping.Category.MISC
            )
    );

    @Override
    public void onInitializeClient() {
        LOGGER.info("CivRails CLIENT initializer loaded!");

        LinkManager.load();
        CivRailsHudConfig.load();

        CivLinkCommand.register();
        CivRailsStatusOverlay.register();

        LOGGER.info("CivRails /civlink command registered!");
        LOGGER.info("CivRails status overlay registered!");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_SETTINGS_KEY.consumeClick()) {
                client.setScreen(new CivRailsSettingsScreen(client.screen));
            }

            HeartbeatManager.tick();
        });

        LOGGER.info("CivRails HUD settings key registered!");
        LOGGER.info("CivRails heartbeat manager registered!");
    }
}
