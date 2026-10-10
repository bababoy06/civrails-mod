package com.civrails.mod.client;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class CivRailsHudConfig {

    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("civrails-hud.properties");

    private static boolean enabled = true;
    private static Position position = Position.TOP_CENTER;

    public enum Position {
        TOP_LEFT(0, 0, "Top left"),
        TOP_CENTER(1, 0, "Top"),
        TOP_RIGHT(2, 0, "Top right"),
        MIDDLE_LEFT(0, 1, "Left"),
        CENTER(1, 1, "Center"),
        MIDDLE_RIGHT(2, 1, "Right"),
        BOTTOM_LEFT(0, 2, "Bottom left"),
        BOTTOM_CENTER(1, 2, "Bottom"),
        BOTTOM_RIGHT(2, 2, "Bottom right");

        private final int horizontal;
        private final int vertical;
        private final String displayName;

        Position(int horizontal, int vertical, String displayName) {
            this.horizontal = horizontal;
            this.vertical = vertical;
            this.displayName = displayName;
        }

        public int horizontal() {
            return horizontal;
        }

        public int vertical() {
            return vertical;
        }

        public String displayName() {
            return displayName;
        }
    }

    private CivRailsHudConfig() {
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
            properties.load(input);
            enabled = Boolean.parseBoolean(properties.getProperty("enabled", "true"));

            String savedPosition = properties.getProperty("position", Position.TOP_CENTER.name());
            try {
                position = Position.valueOf(savedPosition);
            } catch (IllegalArgumentException ignored) {
                position = Position.TOP_CENTER;
            }
        } catch (IOException exception) {
            System.err.println("[CivRails] Could not load HUD settings: " + exception.getMessage());
        }
    }

    public static void save() {
        Properties properties = new Properties();
        properties.setProperty("enabled", Boolean.toString(enabled));
        properties.setProperty("position", position.name());

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (OutputStream output = Files.newOutputStream(CONFIG_PATH)) {
                properties.store(output, "CivRails connection label settings");
            }
        } catch (IOException exception) {
            System.err.println("[CivRails] Could not save HUD settings: " + exception.getMessage());
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        save();
    }

    public static Position getPosition() {
        return position;
    }

    public static void setPosition(Position value) {
        position = value == null ? Position.TOP_CENTER : value;
        save();
    }
}
