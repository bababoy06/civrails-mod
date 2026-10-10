package com.civrails.mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class CivRailsSettingsScreen extends Screen {

    private final Screen parent;

    public CivRailsSettingsScreen(Screen parent) {
        super(Component.literal("CivRails Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        addRenderableWidget(Button.builder(
                toggleLabel(),
                button -> {
                    CivRailsHudConfig.setEnabled(!CivRailsHudConfig.isEnabled());
                    button.setMessage(toggleLabel());
                }
        ).bounds(centerX - 100, 48, 200, 20).build());

        int buttonWidth = 64;
        int gap = 4;
        int totalWidth = buttonWidth * 3 + gap * 2;
        int startX = centerX - totalWidth / 2;
        int startY = 104;

        CivRailsHudConfig.Position[][] positions = {
                {CivRailsHudConfig.Position.TOP_LEFT, CivRailsHudConfig.Position.TOP_CENTER, CivRailsHudConfig.Position.TOP_RIGHT},
                {CivRailsHudConfig.Position.MIDDLE_LEFT, CivRailsHudConfig.Position.CENTER, CivRailsHudConfig.Position.MIDDLE_RIGHT},
                {CivRailsHudConfig.Position.BOTTOM_LEFT, CivRailsHudConfig.Position.BOTTOM_CENTER, CivRailsHudConfig.Position.BOTTOM_RIGHT}
        };

        for (int row = 0; row < positions.length; row++) {
            for (int column = 0; column < positions[row].length; column++) {
                CivRailsHudConfig.Position position = positions[row][column];
                int x = startX + column * (buttonWidth + gap);
                int y = startY + row * 25;

                addRenderableWidget(Button.builder(
                        Component.literal(position.displayName()),
                        button -> CivRailsHudConfig.setPosition(position)
                ).bounds(x, y, buttonWidth, 20).build());
            }
        }

        addRenderableWidget(Button.builder(
                Component.literal("Done"),
                button -> onClose()
        ).bounds(centerX - 100, this.height - 28, 200, 20).build());
    }

    private Component toggleLabel() {
        return Component.literal(
                "Connection label: " + (CivRailsHudConfig.isEnabled() ? "ON" : "OFF")
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Minecraft renders the screen background before calling render().
        // Calling renderBackground() here would apply the blur twice and crash.
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);
        graphics.drawCenteredString(
                this.font,
                Component.literal("Choose where the label appears"),
                this.width / 2,
                82,
                0xFFCCCCCC
        );
        graphics.drawCenteredString(
                this.font,
                Component.literal("Changes are saved automatically"),
                this.width / 2,
                this.height - 42,
                0xFFAAAAAA
        );
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}
