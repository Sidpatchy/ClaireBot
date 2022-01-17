package com.sidpatchy.clairebot.Embed;

import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

public class WelcomeEmbed {

    public static EmbedBuilder getWelcome(Server server) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor())
                .addField("\uD83C\uDF89 Welcome to ClaireBot 3!", "How can you rise, if you have not burned?", true)
                .addField("Usage", "Get started by running `/help`. Need more info on a command? Run `/help <command name>` (ex. `/help user`)", false)
                .addField("Get Support", "You can get help on our [Discord](https://support.clairebot.net/), or by opening an issue on [GitHub](https://github.com/Sidpatchy/ClaireBot)");

        server.getIcon().ifPresent(embed::setThumbnail);

        return embed;
    }
}
