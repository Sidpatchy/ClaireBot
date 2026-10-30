package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.awt.*;

public class ServerInfoEmbed {

    public static EmbedBuilder getServerInfo(LanguageManager languageManager, Guild server, String userID) {
        Color color = Main.getColor(userID);
        String authorName = server.getName();
        String footerLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.ServerID");
        String footerText = footerLabel + ": " + server.getId();
        String ownerLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.Owner");
        String creationDateLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.CreationDate");
        String roleCountLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.RoleCount");
        String memberCountLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.MemberCount");
        String channelCountsLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.ChannelCounts");
        String categoriesLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.Categories");
        String textChannelsLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.TextChannels");
        String voiceChannelsLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.VoiceChannels");

        String channelCountsValue =
                "⦁ " + categoriesLabel + ": " + server.getCategories().size() +
                        "\n⦁ " + textChannelsLabel + ": " + server.getTextChannels().size() +
                        "\n⦁ " + voiceChannelsLabel + ": " + server.getVoiceChannels().size();

        // Build embed (keeps the inline, minimal style)
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(color)
                .setAuthor(authorName)
                .setFooter(footerText);

        if (server.getIconUrl() != null) embed.setThumbnail(server.getIconUrl());

        if (server.getOwner() != null) embed.addField(ownerLabel, server.getOwner().getUser().getAsTag(), false);

        embed.addField(creationDateLabel, "<t:" + server.getTimeCreated().toEpochSecond() + ">", false);

        embed.addField(roleCountLabel, String.valueOf(server.getRoles().size()), false);
        embed.addField(memberCountLabel, String.valueOf(server.getMemberCount()), false);
        embed.addField(channelCountsLabel, channelCountsValue, false);

        return embed;
    }
}
