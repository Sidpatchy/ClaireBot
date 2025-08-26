package com.sidpatchy.clairebot.Embed;

import com.sidpatchy.clairebot.API.Guild;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

import java.io.IOException;

public class WelcomeEmbed {
    private static Logger logger = Main.getLogger();


    public static EmbedBuilder getWelcome(LanguageManager languageManager, Server server) {

        // Initialize the Guild in the database
        Guild guild = new Guild(server.getIdAsString());
        try {
            guild.getGuild();
        } catch (IOException e) {
            logger.error("Error while loading guild data.", e);
        }

        // Temp/localized variables block
        // If your placeholder handler expects {cb.command.help.name}, provide it here
        languageManager.addContext(ContextManager.ContextType.GENERIC, "command.help.name", "help");

        String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.WelcomeEmbed.Title");
        String motto = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.WelcomeEmbed.Motto");
        String usageTitle = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.WelcomeEmbed.UsageTitle");
        String usageDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.WelcomeEmbed.UsageDesc");
        String supportTitle = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.WelcomeEmbed.SupportTitle");
        String supportDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.WelcomeEmbed.SupportDesc");

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor(null))
                .addField(title, motto, true)
                .addField(usageTitle, usageDesc, false)
                .addField(supportTitle, supportDesc);

        server.getIcon().ifPresent(embed::setThumbnail);

        return embed;
    }
}
