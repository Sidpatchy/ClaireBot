package com.sidpatchy.clairebot.Embed;

import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import org.apache.logging.log4j.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.io.IOException;

public class WelcomeEmbed {
    private static final Logger logger = Main.getLogger();


    public static EmbedBuilder getWelcome(LanguageManager languageManager, Guild server) {

        // Initialize the Guild in the database
        com.sidpatchy.clairebot.API.Guild guild = new com.sidpatchy.clairebot.API.Guild(server.getId());
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
                .addField(supportTitle, supportDesc, false);

        if (server.getIconUrl() != null) embed.setThumbnail(server.getIconUrl());

        return embed;
    }
}
