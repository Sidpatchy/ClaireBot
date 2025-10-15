package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import java.awt.Color;

public class ServerInfoEmbed {

    public static EmbedBuilder getServerInfo(LanguageManager languageManager, Server server, String userID) {
        Color color = Main.getColor(userID);
        String authorName = server.getName();
        String footerLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.ServerID");
        String footerText = footerLabel + ": " + server.getIdAsString();
        String ownerLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.Owner");
        String creationDateLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.CreationDate");
        String roleCountLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.RoleCount");
        String memberCountLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.MemberCount");
        String channelCountsLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.ChannelCounts");
        String categoriesLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.Categories");
        String textChannelsLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.TextChannels");
        String voiceChannelsLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ServerInfoEmbed.VoiceChannels");

        String channelCountsValue =
                "⦁ " + categoriesLabel + ": " + server.getChannelCategories().size() +
                        "\n⦁ " + textChannelsLabel + ": " + server.getTextChannels().size() +
                        "\n⦁ " + voiceChannelsLabel + ": " + server.getVoiceChannels().size();

        // Build embed (keeps the inline, minimal style)
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(color)
                .setAuthor(authorName)
                .setFooter(footerText);

        server.getIcon().ifPresent(embed::setThumbnail);

        server.getOwner().ifPresent(owner -> {
            embed.addField(ownerLabel, owner.getDiscriminatedName(), false);
        });

        embed.addField(creationDateLabel, "<t:" + server.getCreationTimestamp().getEpochSecond() + ">");

        embed.addField(roleCountLabel, String.valueOf(server.getRoles().size()), false);
        embed.addField(memberCountLabel, String.valueOf(server.getMemberCount()), false);
        embed.addField(channelCountsLabel, channelCountsValue, false);

        return embed;
    }
}
