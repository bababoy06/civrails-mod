package com.civrails.mod.client;

import com.civrails.mod.CivRails;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;

public final class CivRailsStatusOverlay {

    private static final int HORIZONTAL_PADDING = 5;
    private static final int VERTICAL_PADDING = 2;
    private static final int TOP_MARGIN = 12;
    private static final float SCALE = 0.9f;

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
                    int screenWidth = client.getWindow().getGuiScaledWidth();
                    int x = (screenWidth - Math.round(boxWidth * SCALE)) / 2;
                    int y = TOP_MARGIN;

                    graphics.pose().pushPose();
                    graphics.pose().translate(x, y, 0);
                    graphics.pose().scale(SCALE, SCALE, 1.0f);

                    graphics.fill(
                            0,
                            0,
                            boxWidth,
                            boxHeight,
                            0xA0000000
                    );
                    graphics.drawString(
                            client.font,
                            label,
                            HORIZONTAL_PADDING,
                            VERTICAL_PADDING,
                            textColor,
                            false
                    );

                    graphics.pose().popPose();
                }
        );
    }
}
