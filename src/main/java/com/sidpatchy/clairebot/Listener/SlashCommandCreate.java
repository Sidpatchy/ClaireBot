package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.Robin.Discord.ParseCommands;
import com.sidpatchy.clairebot.Embed.Commands.Regular.*;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.MessageComponents.Regular.ServerPreferencesComponents;
import com.sidpatchy.clairebot.MessageComponents.Regular.UserPreferencesComponents;
import com.sidpatchy.clairebot.MessageComponents.Regular.VotingComponents;
import com.sidpatchy.clairebot.Util.ChannelUtils;
import org.apache.logging.log4j.Logger;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SlashCommandCreate implements SlashCommandCreateListener {

    static ParseCommands parseCommands = new ParseCommands(Main.getCommandsFile());
    Logger logger = Main.getLogger();

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        Server server = slashCommandInteraction.getServer().orElse(null);
        String commandName = slashCommandInteraction.getCommandName();
        User author = slashCommandInteraction.getUser();
        User user = slashCommandInteraction.getArgumentUserValueByName("user").orElse(author);

        if (commandName.equalsIgnoreCase(parseCommands.getCommandName("8ball"))) {
            String query = slashCommandInteraction.getArgumentStringValueByIndex(0).orElse(null);

            if (query == null) {
                return;
            }

            CompletableFuture<InteractionOriginalResponseUpdater> future = slashCommandInteraction.respondLater();

            future.thenAccept(interactionResponse -> {
                try {
                    interactionResponse.addEmbed(EightBallEmbed.getEightBall(query, author));
                    interactionResponse.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        else if (commandName.equalsIgnoreCase(parseCommands.getCommandName("avatar"))) {
            boolean getGlobalAvatar = slashCommandInteraction.getArgumentBooleanValueByName("globalAvatar").orElse(true);
            slashCommandInteraction.createImmediateResponder()
                    .addEmbed(AvatarEmbed.getAvatar(server, user, author, getGlobalAvatar))
                    .respond();
        }
        else if (commandName.equalsIgnoreCase(parseCommands.getCommandName("config"))) {
            String mode = slashCommandInteraction.getArgumentStringValueByName("mode").orElse("user");

            if (mode.equalsIgnoreCase("user")) {
                slashCommandInteraction.createImmediateResponder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(UserPreferencesEmbed.getMainMenu(author))
                        .addComponents(UserPreferencesComponents.getMainMenu())
                        .respond();
            }
            else if (mode.equalsIgnoreCase("server") && server != null) {

                if (server.isAdmin(author)) {
                    slashCommandInteraction.createImmediateResponder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ServerPreferencesEmbed.getMainMenu(author))
                            .addComponents(ServerPreferencesComponents.getMainMenu())
                            .respond();
                }
                else {
                    slashCommandInteraction.createImmediateResponder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ErrorEmbed.getLackingPermissions("You do not have permission to run that command!"))
                            .respond();
                }
            }
        }
        else if (commandName.equalsIgnoreCase(parseCommands.getCommandName("help"))) {
            String command = slashCommandInteraction.getArgumentStringValueByIndex(0).orElse("help");

            try {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(HelpEmbed.getHelp(command, user.getIdAsString()))
                        .respond();
            } catch (FileNotFoundException e) {
                Main.getLogger().error(e);
                Main.getLogger().error("There was an issue locating the commands file at some point in the chain while the help command was running, good luck!");
            }
        }
        else if (commandName.equalsIgnoreCase(parseCommands.getCommandName("info"))) {
            slashCommandInteraction.createImmediateResponder()
                    .addEmbed(InfoEmbed.getInfo(author))
                    .respond();
        }
        else if (commandName.equalsIgnoreCase(parseCommands.getCommandName("leaderboard"))) {
            boolean getGlobal = slashCommandInteraction.getArgumentBooleanValueByName("global").orElse(false);

            if (server == null || getGlobal) {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(LeaderboardEmbed.getLeaderboard("global", author))
                        .respond();
            }
            else {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(LeaderboardEmbed.getLeaderboard(server, author))
                        .respond();
            }
        }
        else if (commandName.equalsIgnoreCase(parseCommands.getCommandName("level"))) {
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
        else if (commandName.equalsIgnoreCase(parseCommands.getCommandName("poll"))) {
            if (slashCommandInteraction.getArgumentStringValueByName("question").orElse(null) == null) {
                try {
                    // LOL how long has this been unimplemented? Not a bad idea tbh 2023-02-16
                    CompletableFuture<Void> pollModal = slashCommandInteraction.respondWithModal("poll", "Create Poll",
                            VotingComponents.getQuestionRow(),
                            VotingComponents.getDetailsRow()
                    );

                    pollModal.exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
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

                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(VotingEmbed.getPoll("POLL", question, allowMultipleChoices, choices, server, author, numChoices))
                        .respond();
                //      .join; Pretty sure this isn't needed
            }
        }
        else if (commandName.equalsIgnoreCase(parseCommands.getCommandName("quote"))) {
            TextChannel channel = slashCommandInteraction.getChannel().orElse(null);
            if (channel == null) {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(ErrorEmbed.getError("NotInAChannel"))
                        .respond();
                return;
            }

            // Construct response and update message
            User finalUser = user;
            slashCommandInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                QuoteEmbed.getQuote(server, finalUser, channel).thenAccept(embed -> {
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
        else if (commandName.equalsIgnoreCase(parseCommands.getCommandName("request"))) {
            if (server == null) {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(ErrorEmbed.getCustomError(Main.getErrorCode("notaserver"),
                                "You must run this command inside a server!"))
                        .respond();
                return;
            }

            if (slashCommandInteraction.getArgumentStringValueByName("question").orElse(null) == null) {
                try {
                    // LOL how long has this been unimplemented? Not a bad idea tbh 2023-02-16
                    CompletableFuture<Void> pollModal = slashCommandInteraction.respondWithModal("request", "Create Request",
                            VotingComponents.getQuestionRow(),
                            VotingComponents.getDetailsRow()
                    );

                    pollModal.exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
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

                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(VotingEmbed.getUserResponse(author, ChannelUtils.getRequestsChannel(server).getMentionTag()))
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();

                ChannelUtils.getRequestsChannel(server).sendMessage(VotingEmbed.getPoll("REQUEST", question, allowMultipleChoices, choices, server, author, numChoices));
            }
        }
        else if (commandName.equalsIgnoreCase("server")) {
            EmbedBuilder embed = null;

            String guildID = slashCommandInteraction.getArgumentStringValueByName("guildID").orElse(null);

            if (server == null && guildID == null) {
                embed = ErrorEmbed.getCustomError(Main.getErrorCode("no-guild-present"), "A guild must be specified. Either run this command in a server or specify a guild ID.");
            }

            if (guildID != null) {
                Server fromGuildID = event.getApi().getServerById(guildID).orElse(null);
                if (fromGuildID != null) {
                    embed = ServerInfoEmbed.getServerInfo(fromGuildID, user.getIdAsString());
                }
                else {
                    embed = ErrorEmbed.getCustomError(Main.getErrorCode("guildID-invalid"), "Either that guild ID is invalid or I'm not a member of the server.");
                }
            }
            else if (server != null) {
                embed = ServerInfoEmbed.getServerInfo(server, user.getIdAsString());
            }

            slashCommandInteraction.createImmediateResponder()
                    .addEmbed(embed)
                    .respond();
        }
        else if (commandName.equalsIgnoreCase("user")) {
            slashCommandInteraction.createImmediateResponder()
                    .addEmbed(UserInfoEmbed.getUser(user, author, server))
                    .respond();
        }
        else if (commandName.equalsIgnoreCase(parseCommands.getCommandName("santa"))) {
            Role role = slashCommandInteraction.getArgumentRoleValueByName("role").orElse(null);

            if (role == null) {
                slashCommandInteraction.createImmediateResponder().addEmbed(
                        ErrorEmbed.getError(Main.getErrorCode("RoleMissing"))
                ).respond();
                return;
            }

            if (!author.canManageRole(role)) {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(ErrorEmbed.getLackingPermissions("Sorry! You don't have the permission to run this " +
                                "command. You must be able to manage the role " + role.getMentionTag() + "."))
                        .respond();
                return;
            }

            slashCommandInteraction.createImmediateResponder().addEmbed(
                    SantaEmbed.getConfirmationEmbed(author)
            ).respond();

            SantaEmbed.getHostMessage(role, author, "", "").send(author);
        }
    }
}
