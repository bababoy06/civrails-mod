package com.civrails.mod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.civrails.mod.auth.LinkManager;

public class CivRailsClient implements ClientModInitializer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger("civrails");

    @Override
    public void onInitializeClient() {
        LOGGER.info("CivRails CLIENT initializer loaded!");

        LinkManager.load();

        CivLinkCommand.register();
        CivRailsStatusOverlay.register();

        LOGGER.info("CivRails /civlink command registered!");
        LOGGER.info("CivRails status overlay registered!");

        ClientTickEvents.END_CLIENT_TICK.register(
                client -> HeartbeatManager.tick()
        );

        LOGGER.info("CivRails heartbeat manager registered!");
    }
}
