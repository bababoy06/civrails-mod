package com.civrails.mod.client;

import com.civrails.mod.api.CivRailsApi;
import com.civrails.mod.auth.LinkManager;
import net.minecraft.client.Minecraft;

public final class HeartbeatManager {

    private static final long HEARTBEAT_INTERVAL_MS = 30_000;

    private static long lastHeartbeat = 0;

    private HeartbeatManager() {
    }

    public static void tick() {
        if (!LinkManager.isLinked()) {
            return;
        }

        Minecraft client = Minecraft.getInstance();

        if (client.player == null) {
            return;
        }

        long now = System.currentTimeMillis();

        if (now - lastHeartbeat < HEARTBEAT_INTERVAL_MS) {
            return;
        }

        lastHeartbeat = now;

        double x = client.player.getX();
        double z = client.player.getZ();
        double yaw = client.player.getYRot();

        String dimension =
                client.player.level()
                        .dimension()
                        .identifier()
                        .toString();

        String authToken = LinkManager.getAuthToken();

        Thread.startVirtualThread(() -> {
            try {
                CivRailsApi.heartbeat(
                        authToken,
                        x,
                        z,
                        yaw,
                        dimension
                );

                System.out.println(
                        "[CivRails] Heartbeat sent: "
                                + x + ", "
                                + z + ", "
                                + yaw + ", "
                                + dimension
                );

            } catch (Exception e) {
                System.err.println(
                        "[CivRails] Heartbeat failed: "
                                + e.getMessage()
                );
            }
        });
    }
}