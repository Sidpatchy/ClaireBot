package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Commands;
import com.sidpatchy.clairebot.Embed.Commands.Regular.*;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.MessageComponents.Regular.ServerPreferencesComponents;
import com.sidpatchy.clairebot.MessageComponents.Regular.UserPreferencesComponents;
import com.sidpatchy.clairebot.MessageComponents.Regular.VotingComponents;
import com.sidpatchy.clairebot.Util.ChannelUtils;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SlashCommandCreate implements SlashCommandCreateListener {

    private static final Logger logger = Main.getLogger();
    private static final Commands commands = Main.getCommands();
    private LanguageManager languageManager;

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        Server server = slashCommandInteraction.getServer().orElse(null);
        String commandName = slashCommandInteraction.getCommandName();
        User author = slashCommandInteraction.getUser();
        User user = slashCommandInteraction.getArgumentUserValueByName("user").orElse(author);
        TextChannel textchannel = slashCommandInteraction.getChannel().orElse(null);

        ContextManager context = new ContextManager(server, textchannel, author, user, null, new HashMap<>());

        // Todo replace reference to en-US with config file parameter
        languageManager = new LanguageManager(Main.getFallbackLocale(), context);

        if (commandName.equalsIgnoreCase(commands.getEightball().getName())) {
            String query = slashCommandInteraction.getArgumentStringValueByIndex(0).orElse(null);

            if (query == null) {
                return;
            }

            CompletableFuture<InteractionOriginalResponseUpdater> future = slashCommandInteraction.respondLater();

            future.thenAccept(interactionResponse -> {
                try {
                    interactionResponse.addEmbed(EightBallEmbed.getEightBall(languageManager, query, author));
                    interactionResponse.update();
                } catch (Exception e) {
                    logger.error("Error while creating eightball embed: ", e);
                }
            });
        }
        else if (commandName.equalsIgnoreCase(commands.getAvatar().getName())) {
            boolean getGlobalAvatar = slashCommandInteraction.getArgumentBooleanValueByName("globalAvatar").orElse(true);
            slashCommandInteraction.createImmediateResponder()
                    .addEmbed(AvatarEmbed.getAvatar(server, user, author, getGlobalAvatar))
                    .respond();
        }
        else if (commandName.equalsIgnoreCase(commands.getConfig().getName())) {
            String mode = slashCommandInteraction.getArgumentStringValueByName("mode").orElse("user");

            if (mode.equalsIgnoreCase("user")) {
                slashCommandInteraction.createImmediateResponder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(UserPreferencesEmbed.getMainMenu(languageManager, author))
                        .addComponents(UserPreferencesComponents.getMainMenu(languageManager))
                        .respond();
            }
            else if (mode.equalsIgnoreCase("server") && server != null) {

                if (server.isAdmin(author)) {
                    slashCommandInteraction.createImmediateResponder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ServerPreferencesEmbed.getMainMenu(languageManager, author))
                            .addComponents(ServerPreferencesComponents.getMainMenu(languageManager))
                            .respond();
                }
                else {
                    slashCommandInteraction.createImmediateResponder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ErrorEmbed.getLackingPermissions(languageManager, "You do not have permission to run that command!"))
                            .respond();
                }
            }
        }
        else if (commandName.equalsIgnoreCase(commands.getHelp().getName())) {
            String command = slashCommandInteraction.getArgumentStringValueByIndex(0).orElse("help");

            try {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(HelpEmbed.getHelp(languageManager, command, user.getIdAsString()))
                        .respond();
            } catch (FileNotFoundException e) {
                Main.getLogger().error(e);
                Main.getLogger().error("There was an issue locating the commands file at some point in the chain while the help command was running, good luck!");
            }
        }
        else if (commandName.equalsIgnoreCase(commands.getInfo().getName())) {
            slashCommandInteraction.createImmediateResponder()
                    .addEmbed(InfoEmbed.getInfo(languageManager, author))
                    .respond();
        }
        else if (commandName.equalsIgnoreCase(commands.getLeaderboard().getName())) {
            boolean getGlobal = slashCommandInteraction.getArgumentBooleanValueByName("global").orElse(false);

            if (server == null || getGlobal) {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(LeaderboardEmbed.getLeaderboard(languageManager, "global", author))
                        .respond();
            }
            else {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(LeaderboardEmbed.getLeaderboard(languageManager, server, author))
                        .respond();
            }
        }
        else if (commandName.equalsIgnoreCase(commands.getLevel().getName())) {
            String serverID;
            if (server == null) {
                serverID = "global";
            }
            else {
                serverID = server.getIdAsString();
            }

            slashCommandInteraction.createImmediateResponder()
                    .addEmbed(LevelEmbed.getLevel(serverID, user))
                    .respond();
        }
        else if (commandName.equalsIgnoreCase(commands.getPoll().getName())) {
            if (slashCommandInteraction.getArgumentStringValueByName("question").orElse(null) == null) {
                try {
                    // LOL how long has this been unimplemented? Not a bad idea tbh 2023-02-16
                    CompletableFuture<Void> pollModal = slashCommandInteraction.respondWithModal("poll", "Create Poll",
                            VotingComponents.getQuestionRow(languageManager),
                            VotingComponents.getDetailsRow(languageManager)
                    );

                    pollModal.exceptionally(e -> {
                        logger.error("Error while creating poll modal: ", e);
                        return null;
                    });
                }
                catch (Exception e) {
                    logger.error("Error while creating poll modal: ", e);
                }
            }
            else {
                String question = slashCommandInteraction.getArgumentStringValueByIndex(0).orElse(null);
                boolean allowMultipleChoices = slashCommandInteraction.getArgumentBooleanValueByIndex(1).orElse(false);
                List<String> choices = new ArrayList<>();

                // Populate choices
                int numChoices = 0;
                for (int i = 0; i < 10; i++) {
                    choices.add(slashCommandInteraction.getArgumentStringValueByName("choice-" + (i + 1)).orElse(null));
                    if (slashCommandInteraction.getArgumentStringValueByName("choice-" + (i + 1)).orElse(null) == null) {
                        numChoices = i;
                        break;
                    }
                }

                int finalNumChoices = numChoices;
                slashCommandInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.addEmbed(VotingEmbed.getPoll(languageManager, "POLL", question, allowMultipleChoices, choices, server, author, finalNumChoices))
                            .update().thenAccept(message -> {
                                if (finalNumChoices > 0) {
                                    String[] numberEmojis = new String[]{"1️⃣","2️⃣","3️⃣","4️⃣","5️⃣","6️⃣","7️⃣","8️⃣","9️⃣","🔟"};
                                    int limit = Math.min(finalNumChoices, numberEmojis.length);
                                    for (int i = 0; i < limit; i++) {
                                        message.addReaction(numberEmojis[i]);
                                    }
                                } else {
                                    message.addReaction("\uD83D\uDC4D"); // 👍 emoji
                                    message.addReaction("\uD83D\uDC4E"); // 👎 emoji
                                    message.addReaction(":vote:706373563564949566"); // Custom emoji
                                }
                            });
                });
            }
        }
        else if (commandName.equalsIgnoreCase(commands.getQuote().getName())) {
            TextChannel channel = slashCommandInteraction.getChannel().orElse(null);
            if (channel == null) {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(ErrorEmbed.getError(languageManager, "NotInAChannel"))
                        .respond();
                return;
            }

            // Construct response and update message
            slashCommandInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                QuoteEmbed.getQuote(languageManager, server, user, channel).thenAccept(embed -> {
                    // Create an ActionRow with a button
                    ActionRow actionRow = ActionRow.of(
                            Button.primary("view_original", "View Original")
                    );

                    // Update the interaction's original response with the embed and action row
                    interactionOriginalResponseUpdater.addEmbed(embed)
                            .addComponents(actionRow)
                            .update();
                });
            });
        }
        else if (commandName.equalsIgnoreCase(commands.getRequest().getName())) {
            if (server == null) {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(ErrorEmbed.getCustomError(languageManager, Main.getErrorCode("notaserver"),
                                "You must run this command inside a server!"))
                        .respond();
                return;
            }

            if (slashCommandInteraction.getArgumentStringValueByName("question").orElse(null) == null) {
                try {
                    // LOL how long has this been unimplemented? Not a bad idea tbh 2023-02-16
                    CompletableFuture<Void> pollModal = slashCommandInteraction.respondWithModal("request", "Create Request",
                            VotingComponents.getQuestionRow(languageManager),
                            VotingComponents.getDetailsRow(languageManager)
                    );

                    pollModal.exceptionally(e -> {
                        logger.error("Error while creating request modal: ", e);
                        return null;
                    });
                }
                catch (Exception e) {
                    logger.error("Error while creating request modal: ", e);
                }
            }
            else {
                String question = slashCommandInteraction.getArgumentStringValueByIndex(0).orElse(null);
                boolean allowMultipleChoices = slashCommandInteraction.getArgumentBooleanValueByIndex(1).orElse(false);
                List<String> choices = new ArrayList<>();

                // Populate choices
                int numChoices = 0;
                for (int i = 0; i < 10; i++) {
                    choices.add(slashCommandInteraction.getArgumentStringValueByName("choice-" + (i + 1)).orElse(null));
                    if (slashCommandInteraction.getArgumentStringValueByName("choice-" + (i + 1)).orElse(null) == null) {
                        numChoices = i;
                        break;
                    }
                }

                // Resolve requests channel safely
                ServerTextChannel requestsChannel = ChannelUtils.getRequestsChannel(server);
                if (requestsChannel == null) {
                    slashCommandInteraction.createImmediateResponder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ErrorEmbed.getCustomError(languageManager, Main.getErrorCode("requestsChannelMissing"), "A requests channel is not configured for this server. An admin can set one in /config server > Requests Channel."))
                            .respond();
                } else {
                    slashCommandInteraction.createImmediateResponder()
                            .addEmbed(VotingEmbed.getUserResponse(languageManager, author, requestsChannel.getMentionTag()))
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();

                    final int finalNumChoicesReq = numChoices;
                    requestsChannel.sendMessage(VotingEmbed.getPoll(languageManager, "REQUEST", question, allowMultipleChoices, choices, server, author, finalNumChoicesReq)).thenAccept(message -> {
                        if (finalNumChoicesReq > 0) {
                            String[] numberEmojis = new String[]{"1️⃣","2️⃣","3️⃣","4️⃣","5️⃣","6️⃣","7️⃣","8️⃣","9️⃣","🔟"};
                            for (int i = 0; i < finalNumChoicesReq; i++) {
                                message.addReaction(numberEmojis[i]);
                            }
                        } else {
                            message.addReaction("\uD83D\uDC4D");
                            message.addReaction("\uD83D\uDC4E");
                            message.addReaction(":vote:706373563564949566");
                        }
                    });
                }
            }
        }
        else if (commandName.equalsIgnoreCase(commands.getServer().getName())) {
            EmbedBuilder embed = null;

            String guildID = slashCommandInteraction.getArgumentStringValueByName("guildID").orElse(null);

            if (server == null && guildID == null) {
                embed = ErrorEmbed.getCustomError(languageManager, Main.getErrorCode("no-guild-present"), "A guild must be specified. Either run this command in a server or specify a guild ID.");
            }

            if (guildID != null) {
                Server fromGuildID = event.getApi().getServerById(guildID).orElse(null);
                if (fromGuildID != null) {
                    embed = ServerInfoEmbed.getServerInfo(languageManager, fromGuildID, user.getIdAsString());
                }
                else {
                    embed = ErrorEmbed.getCustomError(languageManager, Main.getErrorCode("guildID-invalid"), "Either that guild ID is invalid or I'm not a member of the server.");
                }
            }
            else if (server != null) {
                embed = ServerInfoEmbed.getServerInfo(languageManager, server, user.getIdAsString());
            }

            slashCommandInteraction.createImmediateResponder()
                    .addEmbed(embed)
                    .respond();
        }
        else if (commandName.equalsIgnoreCase(commands.getUser().getName())) {
            slashCommandInteraction.createImmediateResponder()
                    .addEmbed(UserInfoEmbed.getUser(languageManager, user, author, server))
                    .respond();
        }
        else if (commandName.equalsIgnoreCase(commands.getSanta().getName())) {
            Role role = slashCommandInteraction.getArgumentRoleValueByName("role").orElse(null);

            if (role == null) {
                slashCommandInteraction.createImmediateResponder().addEmbed(
                        ErrorEmbed.getError(languageManager, Main.getErrorCode("RoleMissing"))
                ).respond();
                return;
            }

            if (!author.canManageRole(role)) {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(ErrorEmbed.getLackingPermissions(languageManager, "Sorry! You don't have the permission to run this " +
                                "command. You must be able to manage the role " + role.getMentionTag() + "."))
                        .respond();
                return;
            }

            slashCommandInteraction.createImmediateResponder().addEmbed(
                    SantaEmbed.getConfirmationEmbed(languageManager, author)
            ).respond();

            SantaEmbed.getHostMessage(languageManager, role, author, "", "").send(author);
        }
    }
}
