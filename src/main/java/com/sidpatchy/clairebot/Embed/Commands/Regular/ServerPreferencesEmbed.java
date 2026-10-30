package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ServerPreferencesEmbed {

    public static EmbedBuilder getMainMenu(LanguageManager languageManager, User author) {
        // Temp/localized variables
        String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.MainMenuTitle");

        return createGenericMenuEmbed(author, title);
    }

    public static EmbedBuilder getNotServerMenu(LanguageManager languageManager) {
        // Temp/localized variables
        String notServerMsg = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.NotAServer");

        return ErrorEmbed.getCustomError(languageManager, Main.getErrorCode("notaserver"), notServerMsg);
    }

    public static EmbedBuilder getRequestsChannelMenu(LanguageManager languageManager, User author) {
        // Temp/localized variables
        String menuName = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.RequestsChannelMenuName");
        String desc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.RequestsChannelDescription");

        return createGenericMenuEmbed(author, menuName)
                .setDescription(desc);
    }

    public static EmbedBuilder getModeratorChannelMenu(LanguageManager languageManager, User author) {
        // Temp/localized variables
        String menuName = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.ModeratorChannelMenuName");
        String desc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.ModeratorChannelDescription");

        return createGenericMenuEmbed(author, menuName)
                .setDescription(desc);
    }

    public static EmbedBuilder getEnforceServerLangMenu(LanguageManager languageManager, User author) {
        // Temp/localized variables
        String menuName = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.EnforceServerLanguageMenuName");

        return createGenericMenuEmbed(author, menuName);
    }

    public static EmbedBuilder getServerLanguageMenu(LanguageManager languageManager, User author) {
        String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.ServerLanguageMenuTitle");
        String desc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.ServerLanguageMenuDesc");
        return createGenericMenuEmbed(author, title).setDescription(desc);
    }

    public static EmbedBuilder getAcknowledgeRequestsChannelChange(LanguageManager languageManager, Guild server, User author, String requestsChannelID) {
        // Resolve channel
        TextChannel channel = Main.getApi().getTextChannelById(requestsChannelID);
        if (channel == null) {
            return ErrorEmbed.getError(languageManager, Main.getErrorCode("channelNotExists"));
        }

        try {
            com.sidpatchy.clairebot.API.Guild guild = new com.sidpatchy.clairebot.API.Guild(server.getId());
            guild.getGuild();
            guild.updateRequestsChannelID(requestsChannelID);

            // Temp/localized variables
            String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.AcknowledgeRequestsChannelChangeTitle");
            String mention = "<#" + channel.getId() + ">";
            languageManager.addContext(ContextManager.ContextType.GENERIC, "cb.channel.requests.mentiontag", mention);
            String desc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.AcknowledgeRequestsChannelChangeDescription");

            return new EmbedBuilder()
                    .setColor(Main.getColor(author.getId()))
                    .setAuthor(title)
                    .setDescription(desc);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorEmbed.getError(languageManager, Main.getErrorCode("updateRequestsChannel"));
        }
    }

    public static EmbedBuilder getAcknowledgeModeratorChannelChange(LanguageManager languageManager, Guild server, User author, String moderatorChannelID) {
        // Resolve channel
        TextChannel channel = Main.getApi().getTextChannelById(moderatorChannelID);
        if (channel == null) {
            return ErrorEmbed.getError(languageManager, Main.getErrorCode("channelNotExists"));
        }

        try {
            com.sidpatchy.clairebot.API.Guild guild = new com.sidpatchy.clairebot.API.Guild(server.getId());
            guild.getGuild();
            guild.updateModeratorMessagesChannelID(moderatorChannelID);

            // Temp/localized variables
            String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.AcknowledgeModeratorChannelChangeTitle");
            String mention = "<#" + channel.getId() + ">";
            languageManager.addContext(ContextManager.ContextType.GENERIC, "cb.channel.moderator.mentiontag", mention);
            String desc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.AcknowledgeModeratorChannelChangeDescription");

            return new EmbedBuilder()
                    .setColor(Main.getColor(author.getId()))
                    .setAuthor(title)
                    .setDescription(desc);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorEmbed.getError(languageManager, Main.getErrorCode("updateModeratorChannel"));
        }
    }

    public static EmbedBuilder getAcknowledgeEnforceServerLanguageUpdate(LanguageManager languageManager, Guild server, User author, String newValue) {
        // Temp/localized variables
        boolean value = Boolean.parseBoolean(newValue);

        try {
            com.sidpatchy.clairebot.API.Guild guild = new com.sidpatchy.clairebot.API.Guild(server.getId());
            guild.getGuild();
            guild.updateEnforceServerLanguage(value);

            String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.AcknowledgeEnforceServerLanguageUpdateTitle");
            String desc = value
                    ? languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.AcknowledgeEnforceServerLanguageUpdateEnforced")
                    : languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.AcknowledgeEnforceServerLanguageUpdateNotEnforced");

            return new EmbedBuilder()
                    .setColor(Main.getColor(author.getId()))
                    .setAuthor(title)
                    .setDescription(desc);

        } catch (Exception e) {
            e.printStackTrace();
            return ErrorEmbed.getError(languageManager, Main.getErrorCode("updateEnforceServerLang"));
        }
    }

    public static EmbedBuilder getAcknowledgeServerLanguageChange(LanguageManager languageManager, Guild server, User author, String languageTag) {
        try {
            com.sidpatchy.clairebot.API.Guild guild = new com.sidpatchy.clairebot.API.Guild(server.getId());
            guild.getGuild();
            guild.updateLocale(languageTag);

            String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.ServerLanguageChanged");
            String desc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerPreferencesEmbed.ServerLanguageChangedDesc");

            return new EmbedBuilder()
                    .setColor(Main.getColor(author.getId()))
                    .setAuthor(title)
                    .setDescription(desc);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorEmbed.getError(languageManager, Main.getErrorCode("updateServerLocale"));
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
                .setColor(Main.getColor(author.getId()))
                .setAuthor(menuName);
    }
}
