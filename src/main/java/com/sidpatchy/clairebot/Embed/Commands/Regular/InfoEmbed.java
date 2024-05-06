package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Main;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

public class InfoEmbed {
    public static EmbedBuilder getInfo(User author) {
        String timeSinceStart = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - Main.getStartMillis(), true, false);
        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .addField("Need Help?", "You can get help by creating an issue on our [GitHub](https://github.com/Sidpatchy/ClaireBot/issues) or by joining our [support server](https://support.clairebot.net/)", true)
                .addField("Add Me to a Server", "Adding me to a server is simple, all you have to do is click [here](https://invite.clairebot.net)", true)
                .addField("GitHub", "ClaireBot is open source, that means you can view all of its code! Check out its [GitHub!](https://github.com/Sidpatchy/ClaireBot)", true)
                .addField("Server Count", "I have enlightened **" + Main.getApi().getServers().size() + "** servers.", true)
                .addField("Version", "I am running ClaireBot **v3.3.2**, released on **2024-08-22**", true)
                .addField("Uptime", "Started on <t:" + Main.getStartMillis() / 1000 + ">" + "\n*" + timeSinceStart + "*", true);
    }
}