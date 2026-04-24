package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

public class InfoEmbed {
    public static EmbedBuilder getInfo(LanguageManager languageManager, User author) {
        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .addField(
                        languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.InfoEmbed.NeedHelp"),
                        languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.InfoEmbed.NeedHelpDetails"),
                        true
                )
                .addField(
                        languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.InfoEmbed.AddToServer"),
                        languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.InfoEmbed.AddToServerDetails"),
                        true
                )
                .addField(
                        languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.InfoEmbed.GitHub"),
                        languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.InfoEmbed.GitHubDetails"),
                        true
                )
                .addField(
                        languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.InfoEmbed.ServerCount"),
                        languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.InfoEmbed.ServerCountDetails"),
                        true
                )
                .addField(
                        languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.InfoEmbed.Version"),
                        languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.InfoEmbed.VersionDetails"),
                        true
                )
                .addField(
                        languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.InfoEmbed.Uptime"),
                        languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.InfoEmbed.UptimeValue"),
                        true
                );
    }
}

