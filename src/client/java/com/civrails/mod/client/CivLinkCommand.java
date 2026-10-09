package com.civrails.mod.client;

import com.civrails.mod.api.CivRailsApi;
import com.civrails.mod.auth.LinkManager;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class CivLinkCommand {

    private CivLinkCommand() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    literal("civlink")
                            .then(argument("code", StringArgumentType.word())
                                    .executes(context -> {

                                        Minecraft client = Minecraft.getInstance();

                                        if (client.player == null) {
                                            return 0;
                                        }

                                        String code = StringArgumentType.getString(
                                                context,
                                                "code"
                                        );

                                        String minecraftUuid =
                                                client.player.getUUID().toString();

                                        client.gui.getChat().addMessage(
                                                Component.literal(
                                                        "Verifying CivRails link..."
                                                )
                                        );

                                        Thread.startVirtualThread(() -> {
                                            try {
                                                CivRailsApi.VerifyResponse response =
                                                        CivRailsApi.verify(
                                                                code,
                                                                minecraftUuid
                                                        );

                                                if (!response.linked()) {
                                                    sendMessage(
                                                            "CivRails link was not completed."
                                                    );
                                                    return;
                                                }

                                                LinkManager.setAuthToken(
                                                        response.authToken()
                                                );

                                                sendMessage(
                                                        "CivRails account linked successfully!"
                                                );

                                            } catch (Exception e) {
                                                sendMessage(
                                                        "CivRails link failed: "
                                                                + e.getMessage()
                                                );
                                            }
                                        });

                                        return 1;
                                    })
                            )
                            .then(literal("unlink")
                                    .executes(context -> {

                                        Minecraft client = Minecraft.getInstance();

                                        if (client.player == null) {
                                            return 0;
                                        }

                                        if (!LinkManager.isLinked()) {
                                            sendMessage(
                                                    "This Minecraft account is not linked to CivRails."
                                            );
                                            return 1;
                                        }

                                        sendMessage(
                                                "Unlinking CivRails account..."
                                        );

                                        Thread.startVirtualThread(() -> {
                                            try {
                                                String authToken =
                                                        LinkManager.getAuthToken();

                                                CivRailsApi.unlink(authToken);

                                                LinkManager.unlink();

                                                sendMessage(
                                                        "CivRails account unlinked successfully."
                                                );

                                            } catch (Exception e) {
                                                sendMessage(
                                                        "CivRails unlink failed: "
                                                                + e.getMessage()
                                                );
                                            }
                                        });

                                        return 1;
                                    })
                            )
            );
        });
    }

    private static void sendMessage(String message) {
        Minecraft.getInstance().execute(() -> {
            Minecraft client = Minecraft.getInstance();

            if (client.player != null) {
                client.gui.getChat().addMessage(
                        Component.literal(message)
                );
            }
        });
    }
}