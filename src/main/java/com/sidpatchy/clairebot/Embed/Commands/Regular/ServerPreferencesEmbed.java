package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.API.Guild;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class ServerPreferencesEmbed {

    public static EmbedBuilder getMainMenu(User author) {
        return createGenericMenuEmbed(author, "Server Configuration Editor");
    }

    public static EmbedBuilder getNotServerMenu() {
        return ErrorEmbed.getCustomError(Main.getErrorCode("notaserver"),
                "You must run this command inside a server!");
    }

    public static EmbedBuilder getRequestsChannelMenu(User author) {
        return createGenericMenuEmbed(author, "Requests Channel");
    }

    public static EmbedBuilder getModeratorChannelMenu(User author) {
        return createGenericMenuEmbed(author, "Moderator Messages Channel");
    }

    public static EmbedBuilder getEnforceServerLangMenu(User author) {
        return createGenericMenuEmbed(author, "Enforce Server Language");
    }

    public static EmbedBuilder getAcknowledgeRequestsChannelChange(Server server, User author, String requestsChannelID) {
        ServerTextChannel channel = Main.getApi().getServerTextChannelById(requestsChannelID).orElse(null);
        if (channel == null) {
            return ErrorEmbed.getError(Main.getErrorCode("channelNotExists"));
        }

        try {
            Guild guild = new Guild(server.getIdAsString());
            guild.getGuild();
            guild.updateRequestsChannelID(requestsChannelID);

            return new EmbedBuilder()
                    .setColor(Main.getColor(author.getIdAsString()))
                    .setAuthor("Requests Channel Changed!")
                    .setDescription("Your requests channel has been changed to " + channel.getMentionTag());
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorEmbed.getError(Main.getErrorCode("updateRequestsChannel"));
        }
    }

    public static EmbedBuilder getAcknowledgeModeratorChannelChange(Server server, User author, String moderatorChannelID) {
        ServerTextChannel channel = Main.getApi().getServerTextChannelById(moderatorChannelID).orElse(null);
        if (channel == null) {
            return ErrorEmbed.getError(Main.getErrorCode("channelNotExists"));
        }

        try {
            Guild guild = new Guild(server.getIdAsString());
            guild.getGuild();
            guild.updateModeratorMessagesChannelID(moderatorChannelID);

            return new EmbedBuilder()
                    .setColor(Main.getColor(author.getIdAsString()))
                    .setAuthor("Moderator Channel Changed!")
                    .setDescription("Your moderator messages channel has been changed to " + channel.getMentionTag());
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorEmbed.getError(Main.getErrorCode("updateModeratorChannel"));
        }
    }

    public static EmbedBuilder getAcknowledgeEnforceServerLanguageUpdate(Server server, User author, String newValue) {
        boolean value = Boolean.parseBoolean(newValue);

        try {
            Guild guild = new Guild(server.getIdAsString());
            guild.getGuild();
            guild.updateEnforceServerLanguage(value);

            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Main.getColor(author.getIdAsString()))
                    .setAuthor("Server Language Preferences Updated!");

            if (value) {
                embed.setDescription("I will now follow the server's language regardless of user preference.");
            }
            else {
                embed.setDescription("I will allow users to set their own language preferences.");
            }

            return embed;

        } catch (Exception e) {
            e.printStackTrace();
            return ErrorEmbed.getError(Main.getErrorCode("updateEnforceServerLang"));
        }
    }

    /**
     * Create a simple embed for settings menus.
     *
     * @param author of the command
     * @param menuName name to display for the menu
     * @return EmbedBuilder
     */
    private static EmbedBuilder createGenericMenuEmbed(User author, String menuName) {
        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor(menuName);
    }
}
