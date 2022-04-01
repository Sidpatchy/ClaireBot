package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

public class ServerInfoEmbed {

    public static EmbedBuilder getServerInfo(Server server) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor())
                .setAuthor(server.getName())
                .setFooter(server.getIdAsString());

        server.getIcon().ifPresent(embed::setThumbnail);

        server.getOwner().ifPresent(owner -> {
            embed.addField("Owner", owner.getDiscriminatedName(), false);
        });

        embed.addField("Creation Date", "<t:" + server.getCreationTimestamp().getEpochSecond() + ">");

        embed.addField("Role Count", String.valueOf(server.getRoles().size()), false);
        embed.addField("Member Count", String.valueOf(server.getMemberCount()), false);
        embed.addField("Channel Counts", "⦁ Categories: " + server.getChannelCategories().size() +
                                                "\n ⦁ Text Channels: " + server.getTextChannels().size() +
                                                "\n ⦁ Voice Channels: " + server.getVoiceChannels().size(), false);


        return embed;
    }
}
