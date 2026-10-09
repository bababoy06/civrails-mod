package com.civrails.mod.client;

import com.civrails.mod.CivRails;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;

public final class CivRailsStatusOverlay {

    private static final int HORIZONTAL_PADDING = 7;
    private static final int VERTICAL_PADDING = 4;
    private static final int TOP_MARGIN = 8;

    private CivRailsStatusOverlay() {
    }

    public static void register() {
        HudElementRegistry.addLast(
                CivRails.id("status_overlay"),
                (graphics, tickCounter) -> {
                    Minecraft client = Minecraft.getInstance();

                    // Keep the overlay visible in-game, but respect Minecraft's
                    // standard F1 setting that hides the HUD.
                    if (client.player == null || client.options.hideGui) {
                        return;
                    }

                    String label;
                    int textColor;

                    switch (HeartbeatManager.getConnectionStatus()) {
                        case NOT_LINKED -> {
                            label = "CivRails | Not linked";
                            textColor = 0xFFFF7777;
                        }
                        case WAITING -> {
                            label = "CivRails | Linked";
                            textColor = 0xFFFFD166;
                        }
                        case CONNECTING -> {
                            label = "CivRails | Checking connection...";
                            textColor = 0xFFFFD166;
                        }
                        case CONNECTED -> {
                            label = "CivRails | Connected";
                            textColor = 0xFF80E080;
                        }
                        case CONNECTION_ISSUE -> {
                            label = "CivRails | Connection issue";
                            textColor = 0xFFFF7777;
                        }
                        default -> {
                            label = "CivRails";
                            textColor = 0xFFFFFFFF;
                        }
                    }

                    int textWidth = client.font.width(label);
                    int boxWidth = textWidth + HORIZONTAL_PADDING * 2;
                    int boxHeight = client.font.lineHeight + VERTICAL_PADDING * 2;
                    int x = (client.getWindow().getGuiScaledWidth() - boxWidth) / 2;
                    int y = TOP_MARGIN;

                    graphics.fill(
                            x,
                            y,
                            x + boxWidth,
                            y + boxHeight,
                            0xA0000000
                    );
                    graphics.drawString(
                            client.font,
                            label,
                            x + HORIZONTAL_PADDING,
                            y + VERTICAL_PADDING,
                            textColor,
                            false
                    );
                }
        );
    }
}
