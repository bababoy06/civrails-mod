package com.civrails.mod.client;

import com.civrails.mod.api.CivRailsApi;
import com.civrails.mod.auth.LinkManager;
import net.minecraft.client.Minecraft;

public final class HeartbeatManager {

    private static final long HEARTBEAT_INTERVAL_MS = 10_000;

    public enum ConnectionStatus {
        NOT_LINKED,
        WAITING,
        CONNECTING,
        CONNECTED,
        CONNECTION_ISSUE
    }

    private static long lastHeartbeat = 0;
    private static volatile ConnectionStatus connectionStatus =
            ConnectionStatus.NOT_LINKED;

    private HeartbeatManager() {
    }

    public static ConnectionStatus getConnectionStatus() {
        if (!LinkManager.isLinked()) {
            return ConnectionStatus.NOT_LINKED;
        }

        ConnectionStatus current = connectionStatus;
        return current == ConnectionStatus.NOT_LINKED
                ? ConnectionStatus.WAITING
                : current;
    }

    public static void tick() {
        if (!LinkManager.isLinked()) {
            connectionStatus = ConnectionStatus.NOT_LINKED;
            lastHeartbeat = 0;
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
        connectionStatus = ConnectionStatus.CONNECTING;

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

                connectionStatus = ConnectionStatus.CONNECTED;

                System.out.println(
                        "[CivRails] Heartbeat sent: "
                                + x + ", "
                                + z + ", "
                                + yaw + ", "
                                + dimension
                );

            } catch (Exception e) {
                connectionStatus = ConnectionStatus.CONNECTION_ISSUE;
                System.err.println(
                        "[CivRails] Heartbeat failed: "
                                + e.getMessage()
                );
            }
        });
    }
}
