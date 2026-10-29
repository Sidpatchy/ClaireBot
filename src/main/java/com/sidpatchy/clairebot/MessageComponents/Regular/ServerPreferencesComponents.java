package com.sidpatchy.clairebot.MessageComponents.Regular;

import com.sidpatchy.clairebot.Lang.LanguageManager;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.ActionRowBuilder;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.server.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerPreferencesComponents {

    public static ActionRow getMainMenu(LanguageManager languageManager) {
        String placeholder = languageManager.getLocalizedString("ClaireLang.Generic.ClickToDisplaySettings");

        String requestsLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.RequestsChannel");
        String requestsDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.RequestsChannelDescription");

        String modLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.ModeratorMessagesChannel");
        String modDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.ModeratorMessagesChannelDescription");

        String enforceLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.EnforceServerLanguage");
        String enforceDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.EnforceServerLanguageDescription");

        String serverLangLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.ServerLanguage");
        String serverLangDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.ServerLanguageDescription");

        return new ActionRowBuilder()
                .addComponents(
                        SelectMenu.create("server-settings", placeholder, 1, 1,
                                Arrays.asList(
                                        // Keep values stable for interaction handlers
                                        SelectMenuOption.create(requestsLabel, "Requests Channel", requestsDesc),
                                        SelectMenuOption.create(modLabel, "Moderator Messages Channel", modDesc),
                                        SelectMenuOption.create(enforceLabel, "Enforce Server Language", enforceDesc),
                                        SelectMenuOption.create(serverLangLabel, "Server Language", serverLangDesc)
                                ))
                ).build();
    }

    public static ActionRow getRequestsChannelMenu(LanguageManager languageManager, Server server) {
        return getChannelListActionRow(languageManager, server, "requestsChannel");
    }

    public static ActionRow getModeratorChannelMenu(LanguageManager languageManager, Server server) {
        return getChannelListActionRow(languageManager, server, "moderatorChannel");
    }

    public static ActionRow getEnforceServerLanguageMenu(LanguageManager languageManager) {
        String placeholder = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.EnforceServerLanguagePlaceholder");
        String trueText = languageManager.getLocalizedString("ClaireLang.Generic.True");
        String falseText = languageManager.getLocalizedString("ClaireLang.Generic.False");

        return new ActionRowBuilder()
                .addComponents(
                        SelectMenu.create("enforceServerLanguage", placeholder, 1, 1,
                                Arrays.asList(
                                        // Localized labels with stable boolean values
                                        SelectMenuOption.create(trueText, "true"),
                                        SelectMenuOption.create(falseText, "false")
                                ))
                ).build();
    }

    public static ActionRow getServerLanguageMenu(LanguageManager languageManager) {
        // Server language placeholder
        String placeholder = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.ServerLanguagePlaceholder");

        java.io.File dir = new java.io.File(com.sidpatchy.clairebot.Main.getTranslationsPath());
        List<SelectMenuOption> options = new ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            java.io.File[] files = dir.listFiles((d, name) -> name.startsWith("lang_") && name.endsWith(".yml"));
            if (files != null) {
                Arrays.sort(files, java.util.Comparator.comparing(java.io.File::getName));
                for (java.io.File f : files) {
                    String name = f.getName();
                    String tag = name.substring("lang_".length(), name.length() - ".yml".length());
                    String label = tag;
                    options.add(SelectMenuOption.create(label, tag));
                }
            }
        }
        if (options.isEmpty()) {
            String tag = com.sidpatchy.clairebot.Main.getFallbackLocale().toLanguageTag();
            options.add(SelectMenuOption.create(tag, tag));
        }
        return new ActionRowBuilder()
                .addComponents(SelectMenu.create("server-language", placeholder, 1, 1, options))
                .build();
    }

    /**
     * Create a select menu with a list of the server's channels.
     *
     * @param server The server the list should be generated for
     * @param customId The ID of the SelectMenu
     * @param languageManager Localization provider
     * @return ActionRow with SelectMenu
     */
    private static ActionRow getChannelListActionRow(LanguageManager languageManager, Server server, String customId) {
        List<ServerTextChannel> channels = server.getTextChannels();
        List<SelectMenuOption> options = new ArrayList<>();
        int count = 0;
        for (ServerTextChannel channel : channels) {
            String channelName = channel.getName().replaceAll("[^\\p{ASCII}]", ""); // Remove non-ASCII characters
            if (channelName.length() > 25) {
                channelName = channelName.substring(0, 25); // Truncate channel name to 25 characters
            }
            if (count < 25) {
                options.add(SelectMenuOption.create(channelName, channel.getIdAsString()));
                count++;
            }
        }

        String placeholder = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.SelectAChannelPlaceholder");

        return new ActionRowBuilder()
                .addComponents(
                        SelectMenu.create(customId, placeholder, 1, 1, options)
                ).build();
    }

}
