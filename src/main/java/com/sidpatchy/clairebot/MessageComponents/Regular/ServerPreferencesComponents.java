package com.sidpatchy.clairebot.MessageComponents.Regular;

import com.sidpatchy.clairebot.Lang.LanguageManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.selections.SelectOption;

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

        return ActionRow.of(StringSelectMenu.create("server-settings").setPlaceholder(placeholder)
                .addOptions(
                        // Keep values stable for interaction handlers
                        SelectOption.of(requestsLabel, "Requests Channel").withDescription(requestsDesc),
                        SelectOption.of(modLabel, "Moderator Messages Channel").withDescription(modDesc),
                        SelectOption.of(enforceLabel, "Enforce Server Language").withDescription(enforceDesc),
                        SelectOption.of(serverLangLabel, "Server Language").withDescription(serverLangDesc)
                ).build());
    }

    public static ActionRow getRequestsChannelMenu(LanguageManager languageManager, Guild server) {
        return getChannelListActionRow(languageManager, server, "requestsChannel");
    }

    public static ActionRow getModeratorChannelMenu(LanguageManager languageManager, Guild server) {
        return getChannelListActionRow(languageManager, server, "moderatorChannel");
    }

    public static ActionRow getEnforceServerLanguageMenu(LanguageManager languageManager) {
        String placeholder = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.EnforceServerLanguagePlaceholder");
        String trueText = languageManager.getLocalizedString("ClaireLang.Generic.True");
        String falseText = languageManager.getLocalizedString("ClaireLang.Generic.False");

        return ActionRow.of(StringSelectMenu.create("enforceServerLanguage").setPlaceholder(placeholder)
                .addOptions(
                        // Localized labels with stable boolean values
                        SelectOption.of(trueText, "true"),
                        SelectOption.of(falseText, "false")
                ).build());
    }

    public static ActionRow getServerLanguageMenu(LanguageManager languageManager) {
        // Server language placeholder
        String placeholder = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.ServerLanguagePlaceholder");

        java.io.File dir = new java.io.File(com.sidpatchy.clairebot.Main.getTranslationsPath());
        List<SelectOption> options = new ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            java.io.File[] files = dir.listFiles((d, name) -> name.startsWith("lang_") && name.endsWith(".yml"));
            if (files != null) {
                Arrays.sort(files, java.util.Comparator.comparing(java.io.File::getName));
                for (java.io.File f : files) {
                    String name = f.getName();
                    String tag = name.substring("lang_".length(), name.length() - ".yml".length());
                    String label = tag;
                    options.add(SelectOption.of(label, tag));
                }
            }
        }
        if (options.isEmpty()) {
            String tag = com.sidpatchy.clairebot.Main.getFallbackLocale().toLanguageTag();
            options.add(SelectOption.of(tag, tag));
        }
        return ActionRow.of(StringSelectMenu.create("server-language").setPlaceholder(placeholder)
                .addOptions(options).build());
    }

    /**
     * Create a select menu with a list of the server's channels.
     *
     * @param server The server the list should be generated for
     * @param customId The ID of the SelectMenu
     * @param languageManager Localization provider
     * @return ActionRow with SelectMenu
     */
    private static ActionRow getChannelListActionRow(LanguageManager languageManager, Guild server, String customId) {
        List<TextChannel> channels = server.getTextChannels();
        List<SelectOption> options = new ArrayList<>();
        int count = 0;
        for (TextChannel channel : channels) {
            String channelName = channel.getName().replaceAll("[^\\p{ASCII}]", ""); // Remove non-ASCII characters
            if (channelName.length() > 25) {
                channelName = channelName.substring(0, 25); // Truncate channel name to 25 characters
            }
            if (count < 25) {
                options.add(SelectOption.of(channelName, channel.getId()));
                count++;
            }
        }

        String placeholder = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.ServerPreferences.SelectAChannelPlaceholder");

        return ActionRow.of(StringSelectMenu.create(customId).setPlaceholder(placeholder)
                .addOptions(options).build());
    }

}
