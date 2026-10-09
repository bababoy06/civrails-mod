package com.civrails.mod.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CivRailsApi {

    private static final String API_URL =
            "https://api.civrails.com/api/v1/minecraft";

    private static final HttpClient HTTP_CLIENT =
            HttpClient.newHttpClient();

    private static final Gson GSON =
            new Gson();

    public static VerifyResponse verify(
            String code,
            String minecraftUuid
    ) throws IOException, InterruptedException {

        JsonObject body = new JsonObject();

        body.addProperty("code", code);
        body.addProperty("minecraftUuid", minecraftUuid);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/verify"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        GSON.toJson(body)
                ))
                .build();

        HttpResponse<String> response =
                HTTP_CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        JsonObject json =
                GSON.fromJson(response.body(), JsonObject.class);

        if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

            String error = json.has("error")
                    ? json.get("error").getAsString()
                    : "Unknown API error";

            throw new IOException(error);
        }

        return new VerifyResponse(
                json.get("linked").getAsBoolean(),
                json.get("minecraftUuid").getAsString(),
                json.get("authToken").getAsString()
        );
    }

    public static void heartbeat(
            String authToken,
            double x,
            double z,
            double yaw,
            String dimension
    ) throws IOException, InterruptedException {

        JsonObject body = new JsonObject();

        body.addProperty("x", x);
        body.addProperty("z", z);
        body.addProperty("yaw", yaw);
        body.addProperty("dimension", dimension);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/heartbeat"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .POST(HttpRequest.BodyPublishers.ofString(
                        GSON.toJson(body)
                ))
                .build();

        HttpResponse<String> response =
                HTTP_CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

            JsonObject json =
                    GSON.fromJson(response.body(), JsonObject.class);

            String error = json.has("error")
                    ? json.get("error").getAsString()
                    : "Unknown API error";

            throw new IOException(error);
        }
    }

    public static void unlink(
            String authToken
    ) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/unlink-client"))
                .header("Authorization", "Bearer " + authToken)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response =
                HTTP_CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        JsonObject json =
                GSON.fromJson(response.body(), JsonObject.class);

        if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

            String error = json.has("error")
                    ? json.get("error").getAsString()
                    : "Unknown API error";

            throw new IOException(error);
        }
    }

    public record VerifyResponse(
            boolean linked,
            String minecraftUuid,
            String authToken
    ) {}
}