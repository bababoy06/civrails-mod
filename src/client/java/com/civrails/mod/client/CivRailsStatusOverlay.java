package com.civrails.mod.client;

import com.civrails.mod.CivRails;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class CivRailsStatusOverlay {

    private static final int HORIZONTAL_PADDING = 2;
    private static final int VERTICAL_PADDING = 2;
    private static final int SCREEN_MARGIN = 4;
    private static final int CORNER_RADIUS = 3;
    private static final float SCALE = 0.9f;

    private CivRailsStatusOverlay() {
    }

    public static void register() {
        HudElementRegistry.addLast(
                CivRails.id("status_overlay"),
                (graphics, tickCounter) -> {
                    Minecraft client = Minecraft.getInstance();

                    if (!CivRailsHudConfig.isEnabled()
                            || client.player == null
                            || client.options.hideGui) {
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
                    int screenHeight = client.getWindow().getGuiScaledHeight();

                    CivRailsHudConfig.Position position = CivRailsHudConfig.getPosition();
                    int scaledWidth = Math.round(boxWidth * SCALE);
                    int scaledHeight = Math.round(boxHeight * SCALE);

                    int x = switch (position.horizontal()) {
                        case 0 -> SCREEN_MARGIN;
                        case 1 -> (screenWidth - scaledWidth) / 2;
                        default -> screenWidth - scaledWidth - SCREEN_MARGIN;
                    };
                    int y = switch (position.vertical()) {
                        case 0 -> SCREEN_MARGIN;
                        case 1 -> (screenHeight - scaledHeight) / 2;
                        default -> screenHeight - scaledHeight - SCREEN_MARGIN;
                    };

                    graphics.pose().pushMatrix();
                    graphics.pose().translate((float) x, (float) y);
                    graphics.pose().scale(SCALE, SCALE);

                    drawRoundedBackground(
                            graphics,
                            0,
                            0,
                            boxWidth,
                            boxHeight,
                            CORNER_RADIUS,
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

                    graphics.pose().popMatrix();
                }
        );
    }

    private static void drawRoundedBackground(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            int radius,
            int color
    ) {
        int actualRadius = Math.min(radius, Math.min(width / 2, height / 2));

        for (int row = 0; row < height; row++) {
            int inset = 0;

            if (row < actualRadius) {
                inset = actualRadius - row;
            } else if (row >= height - actualRadius) {
                inset = actualRadius - (height - 1 - row);
            }

            graphics.fill(
                    x + inset,
                    y + row,
                    x + width - inset,
                    y + row + 1,
                    color
            );
        }
    }
}
