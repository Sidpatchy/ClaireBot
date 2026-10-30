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
import com.sidpatchy.clairebot.Util.Voting.VotingFlow;
import org.apache.logging.log4j.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.modals.Modal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SlashCommandCreate extends ListenerAdapter {

    private static final Logger logger = Main.getLogger();
    private static final Commands commands = Main.getCommands();
    private LanguageManager languageManager;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandInteractionEvent slashCommandInteraction = event;
        Guild server = slashCommandInteraction.getGuild();
        String commandName = slashCommandInteraction.getName();
        User author = slashCommandInteraction.getUser();
        User user = slashCommandInteraction.getOption("user") == null ? author : slashCommandInteraction.getOption("user").getAsUser();
        MessageChannel textchannel = slashCommandInteraction.getChannel();

        ContextManager context = new ContextManager(server, textchannel, author, user, null, new HashMap<>());

        // Todo replace reference to en-US with config file parameter
        languageManager = new LanguageManager(Main.getFallbackLocale(), context);

        if (commandName.equalsIgnoreCase(commands.getEightball().getName())) {
            String query = slashCommandInteraction.getOption("query") == null ? null : slashCommandInteraction.getOption("query").getAsString();

            if (query == null) {
                return;
            }

            slashCommandInteraction.deferReply().queue(interactionResponse -> {
                try {
                    interactionResponse.editOriginalEmbeds(EightBallEmbed.getEightBall(languageManager, query, author).build()).queue();
                } catch (Exception e) {
                    logger.error("Error while creating eightball embed: ", e);
                }
            });
        }
        else if (commandName.equalsIgnoreCase(commands.getAvatar().getName())) {
            boolean getGlobalAvatar = slashCommandInteraction.getOption("globalavatar") == null || slashCommandInteraction.getOption("globalavatar").getAsBoolean();
            slashCommandInteraction.replyEmbeds(AvatarEmbed.getAvatar(server, user, author, getGlobalAvatar).build()).queue();
        }
        else if (commandName.equalsIgnoreCase(commands.getConfig().getName())) {
            String mode = slashCommandInteraction.getOption("mode") == null ? "user" : slashCommandInteraction.getOption("mode").getAsString();

            if (mode.equalsIgnoreCase("user")) {
                slashCommandInteraction.replyEmbeds(UserPreferencesEmbed.getMainMenu(languageManager, author).build())
                        .addComponents(UserPreferencesComponents.getMainMenu(languageManager)).setEphemeral(true).queue();
            }
            else if (mode.equalsIgnoreCase("server") && server != null) {

                if (event.getMember() != null && event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    slashCommandInteraction.replyEmbeds(ServerPreferencesEmbed.getMainMenu(languageManager, author).build())
                            .addComponents(ServerPreferencesComponents.getMainMenu(languageManager)).setEphemeral(true).queue();
                }
                else {
                    slashCommandInteraction.replyEmbeds(ErrorEmbed.getLackingPermissions(languageManager, "You do not have permission to run that command!").build())
                            .setEphemeral(true).queue();
                }
            }
        }
        else if (commandName.equalsIgnoreCase(commands.getHelp().getName())) {
            String command = slashCommandInteraction.getOption("command-name") == null
                    ? commands.getHelp().getName() : slashCommandInteraction.getOption("command-name").getAsString();
            slashCommandInteraction.replyEmbeds(HelpEmbed.getHelp(languageManager, command, user.getId()).build()).queue();
        }
        else if (commandName.equalsIgnoreCase(commands.getInfo().getName())) {
            slashCommandInteraction.replyEmbeds(InfoEmbed.getInfo(languageManager, author).build()).queue();
        }
        else if (commandName.equalsIgnoreCase(commands.getLeaderboard().getName())) {
            boolean getGlobal = slashCommandInteraction.getOption("global") != null && slashCommandInteraction.getOption("global").getAsBoolean();

            if (server == null || getGlobal) {
                slashCommandInteraction.replyEmbeds(LeaderboardEmbed.getLeaderboard(languageManager, "global", author).build()).queue();
            }
            else {
                slashCommandInteraction.replyEmbeds(LeaderboardEmbed.getLeaderboard(languageManager, server, author).build()).queue();
            }
        }
        else if (commandName.equalsIgnoreCase(commands.getLevel().getName())) {
            String serverID;
            if (server == null) {
                serverID = "global";
            }
            else {
                serverID = server.getId();
            }

            slashCommandInteraction.replyEmbeds(LevelEmbed.getLevel(serverID, user).build()).queue();
        }
        else if (commandName.equalsIgnoreCase(commands.getPoll().getName())) {
            if (server == null) {
                slashCommandInteraction.replyEmbeds(ErrorEmbed.getCustomError(languageManager, Main.getErrorCode("notaserver"),
                        "You must run this command inside a server!").build()).queue();
                return;
            }
            if (slashCommandInteraction.getOption("question") == null) {
                try {
                    slashCommandInteraction.replyModal(Modal.create("poll", "Create Poll")
                            .addComponents(VotingComponents.getQuestionRow(languageManager), VotingComponents.getDetailsRow(languageManager))
                            .build()).queue();
                }
                catch (Exception e) {
                    logger.error("Error while creating poll modal: ", e);
                }
            }
            else {
                String question = slashCommandInteraction.getOption("question").getAsString();
                boolean allowMultipleChoices = slashCommandInteraction.getOption("allow-multiple-choices") != null && slashCommandInteraction.getOption("allow-multiple-choices").getAsBoolean();
                List<String> choices = new ArrayList<>();

                // Populate choices
                int numChoices = 0;
                for (int i = 0; i < 10; i++) {
                    OptionMapping option = slashCommandInteraction.getOption("choice-" + (i + 1));
                    String choice = option == null ? null : option.getAsString();
                    if (choice == null || choice.isBlank()) break;
                    choices.add(choice);
                    numChoices++;
                }

                int finalNumChoices = numChoices;
                slashCommandInteraction.deferReply().queue(hook -> hook.editOriginalEmbeds(
                        VotingEmbed.getPoll(languageManager, "POLL", question, allowMultipleChoices, choices, server, author, finalNumChoices).build())
                        .queue(message -> VotingFlow.addReactions(message, finalNumChoices)));
            }
        }
        else if (commandName.equalsIgnoreCase(commands.getQuote().getName())) {
            MessageChannel channel = slashCommandInteraction.getChannel();
            if (channel == null) {
                slashCommandInteraction.replyEmbeds(ErrorEmbed.getError(languageManager, "NotInAChannel").build()).queue();
                return;
            }

            // Construct response and update message
            slashCommandInteraction.deferReply().queue(interactionOriginalResponseUpdater -> {
                QuoteEmbed.getQuote(languageManager, server, user, channel).thenAccept(embed -> {
                    // Create an ActionRow with a button
                    ActionRow actionRow = ActionRow.of(
                            Button.primary("view_original", "View Original")
                    );

                    // Update the interaction's original response with the embed and action row
                    interactionOriginalResponseUpdater.editOriginalEmbeds(embed.build())
                            .setComponents(actionRow).queue();
                });
            });
        }
        else if (commandName.equalsIgnoreCase(commands.getRequest().getName())) {
            if (server == null) {
                slashCommandInteraction.replyEmbeds(ErrorEmbed.getCustomError(languageManager, Main.getErrorCode("notaserver"),
                        "You must run this command inside a server!").build()).queue();
                return;
            }

            if (slashCommandInteraction.getOption("question") == null) {
                try {
                    slashCommandInteraction.replyModal(Modal.create("request", "Create Request")
                            .addComponents(VotingComponents.getQuestionRow(languageManager), VotingComponents.getDetailsRow(languageManager))
                            .build()).queue();
                }
                catch (Exception e) {
                    logger.error("Error while creating request modal: ", e);
                }
            }
            else {
                String question = slashCommandInteraction.getOption("question").getAsString();
                boolean allowMultipleChoices = slashCommandInteraction.getOption("allow-multiple-choices") != null && slashCommandInteraction.getOption("allow-multiple-choices").getAsBoolean();
                List<String> choices = new ArrayList<>();

                // Populate choices
                int numChoices = 0;
                for (int i = 0; i < 10; i++) {
                    OptionMapping option = slashCommandInteraction.getOption("choice-" + (i + 1));
                    String choice = option == null ? null : option.getAsString();
                    if (choice == null || choice.isBlank()) break;
                    choices.add(choice);
                    numChoices++;
                }

                // Resolve requests channel safely
                TextChannel requestsChannel = ChannelUtils.getRequestsChannel(server);
                if (requestsChannel == null) {
                    slashCommandInteraction.replyEmbeds(ErrorEmbed.getCustomError(languageManager, Main.getErrorCode("requestsChannelMissing"), "A requests channel is not configured for this server. An admin can set one in /config server > Requests Channel.").build())
                            .setEphemeral(true).queue();
                } else {
                    slashCommandInteraction.replyEmbeds(VotingEmbed.getUserResponse(languageManager, author, requestsChannel.getAsMention()).build())
                            .setEphemeral(true).queue();

                    final int finalNumChoicesReq = numChoices;
                    requestsChannel.sendMessageEmbeds(VotingEmbed.getPoll(languageManager, "REQUEST", question, allowMultipleChoices, choices, server, author, finalNumChoicesReq).build())
                            .queue(message -> VotingFlow.addReactions(message, finalNumChoicesReq));
                }
            }
        }
        else if (commandName.equalsIgnoreCase(commands.getServer().getName())) {
            EmbedBuilder embed = null;

            String guildID = slashCommandInteraction.getOption("guildid") == null ? null : slashCommandInteraction.getOption("guildid").getAsString();

            if (server == null && guildID == null) {
                embed = ErrorEmbed.getCustomError(languageManager, Main.getErrorCode("no-guild-present"), "A guild must be specified. Either run this command in a server or specify a guild ID.");
            }

            if (guildID != null) {
                Guild fromGuildID = null;
                try {
                    fromGuildID = event.getJDA().getGuildById(guildID);
                } catch (NumberFormatException ignored) { }
                if (fromGuildID != null) {
                    embed = ServerInfoEmbed.getServerInfo(languageManager, fromGuildID, user.getId());
                }
                else {
                    embed = ErrorEmbed.getCustomError(languageManager, Main.getErrorCode("guildID-invalid"), "Either that guild ID is invalid or I'm not a member of the server.");
                }
            }
            else if (server != null) {
                embed = ServerInfoEmbed.getServerInfo(languageManager, server, user.getId());
            }

            slashCommandInteraction.replyEmbeds(embed.build()).queue();
        }
        else if (commandName.equalsIgnoreCase(commands.getUser().getName())) {
            slashCommandInteraction.replyEmbeds(UserInfoEmbed.getUser(languageManager, user, author, server).build()).queue();
        }
        else if (commandName.equalsIgnoreCase(commands.getSanta().getName())) {
            Role role = slashCommandInteraction.getOption("role") == null ? null : slashCommandInteraction.getOption("role").getAsRole();

            if (role == null) {
                slashCommandInteraction.replyEmbeds(ErrorEmbed.getError(languageManager, Main.getErrorCode("RoleMissing")).build()).queue();
                return;
            }

            if (event.getMember() == null || !event.getMember().hasPermission(Permission.MANAGE_ROLES) || !event.getMember().canInteract(role)) {
                slashCommandInteraction.replyEmbeds(ErrorEmbed.getLackingPermissions(languageManager, "Sorry! You don't have the permission to run this " +
                        "command. You must be able to manage the role " + role.getAsMention() + ".").build()).queue();
                return;
            }

            slashCommandInteraction.replyEmbeds(SantaEmbed.getConfirmationEmbed(languageManager, author).build()).queue();

            author.openPrivateChannel().flatMap(channel -> channel.sendMessage(SantaEmbed.getHostMessage(languageManager, role, author, "", "").build())).queue();
        }
    }
}
