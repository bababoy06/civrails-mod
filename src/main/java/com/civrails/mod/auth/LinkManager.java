package com.civrails.mod.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LinkManager {

    private static final Gson GSON = new Gson();

    private static final Path AUTH_FILE =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("civrails")
                    .resolve("auth.json");

    private static String authToken;

    private LinkManager() {
    }

    public static void load() {
        try {
            if (!Files.exists(AUTH_FILE)) {
                return;
            }

            String jsonText = Files.readString(AUTH_FILE);

            JsonObject json =
                    GSON.fromJson(jsonText, JsonObject.class);

            if (json != null && json.has("authToken")) {
                authToken =
                        json.get("authToken").getAsString();
            }

        } catch (Exception e) {
            System.err.println(
                    "[CivRails] Failed to load auth token: "
                            + e.getMessage()
            );

            authToken = null;
        }
    }

    public static void setAuthToken(String token) {
        authToken = token;
        save();
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static boolean isLinked() {
        return authToken != null && !authToken.isBlank();
    }

    public static void unlink() {
        authToken = null;

        try {
            Files.deleteIfExists(AUTH_FILE);
        } catch (IOException e) {
            System.err.println(
                    "[CivRails] Failed to remove auth file: "
                            + e.getMessage()
            );
        }
    }

    private static void save() {
        try {
            Files.createDirectories(AUTH_FILE.getParent());

            JsonObject json = new JsonObject();
            json.addProperty("authToken", authToken);

            Files.writeString(
                    AUTH_FILE,
                    GSON.toJson(json)
            );

        } catch (IOException e) {
            System.err.println(
                    "[CivRails] Failed to save auth token: "
                            + e.getMessage()
            );
        }
    }
}