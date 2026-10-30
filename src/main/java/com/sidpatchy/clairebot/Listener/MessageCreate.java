package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.API.APIUser;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class MessageCreate extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String messageContent = message.getContentRaw();
        Guild server = message.isFromGuild() ? message.getGuild() : null;
        User messageAuthor = message.getAuthor();
        User user = messageAuthor;
        APIUser apiUser = new APIUser(messageAuthor.getId());
        MessageChannel textChannel = message.getChannel();

        // Some messages (e.g., webhooks/system) have no user; skip processing to avoid NPEs in localization
        if (user == null || user.isSystem() || message.isWebhookMessage()) {
            return;
        }

        ContextManager context = new ContextManager(server, textChannel, user, user, message, new HashMap<>());
        LanguageManager languageManager = new LanguageManager(Main.getFallbackLocale(), context);

        // Skip bot accounts and ourselves.
        if (messageAuthor.isBot() || messageAuthor.getIdLong() == event.getJDA().getSelfUser().getIdLong() || messageAuthor.getId().equalsIgnoreCase("704244031772950528") || messageAuthor.getId().equalsIgnoreCase("848024760789237810")) {
            Main.getLogger().debug("Detected bot user, skipping onMesssage checks!");
            return;
        }

        // ClaireBot on top!!
        List<String> onTopTriggers = languageManager.getLocalizedList("ClaireLang.Embed.Commands.Regular.EightBallEmbed.OnTopTriggers");
        List<String> onTopResponses = languageManager.getLocalizedList("ClaireLang.Embed.Commands.Regular.EightBallEmbed.ClaireBotOnTopResponses");
        for (String trigger : onTopTriggers) {
            String regex = "\\b" + Pattern.quote(trigger.toUpperCase()) + "\\b.*"; // match trigger followed by anything
            if (messageContent.toUpperCase().matches(regex)) {
                Random random = new Random();
                int rand = random.nextInt(onTopResponses.size());
                
                // Reply without pinging the author.
                message.reply(onTopResponses.get(rand))
                        .setAllowedMentions(List.of())
                        .queue();

                break;
            }
        }

        // pls ban
        List<String> plsBanTriggers = languageManager.getLocalizedList("ClaireLang.PlsBan.PlsBanTriggers");
        List<String> plsBanResponses = languageManager.getLocalizedList("ClaireLang.PlsBan.PlsBanResponses");
        String escapedBotId = Pattern.quote("<@" + Main.getApi().getSelfUser().getId() + ">");

        for (String trigger : plsBanTriggers) {
            String regex = "(?i)" + escapedBotId + "\\s*" + Pattern.quote(trigger) + ".*";
            if (messageContent.toUpperCase().matches(regex)) {
                Random random = new Random();
                int rand = random.nextInt(plsBanResponses.size());

                // Reply without pinging the author.
                message.reply(plsBanResponses.get(rand))
                        .setAllowedMentions(List.of())
                        .queue();

                break;
            }
        }

        // Zerfas react
        for (String trigger : Main.getZerfas()) {
            String regex = ".*\\b" + Pattern.quote(trigger.toUpperCase()) + "\\b.*"; // match trigger led/followed by anything
            if (messageContent.toUpperCase().matches(regex)) {
                Guild zerfasEmojiServer = event.getJDA().getGuildById(Main.getZerfasEmojiServerID());
                RichCustomEmoji zerfasEmoji = zerfasEmojiServer == null ? null : zerfasEmojiServer.getEmojiById(Main.getZerfasEmojiID());
                if (zerfasEmoji != null) message.addReaction(zerfasEmoji).queue();
            }
        }

        // Grant between 0 and 8 points
        if (server != null) {
            int pointsToGrant = ThreadLocalRandom.current().nextInt(9);
            try {
                Map<String, Integer> guildPointsToUpdate = Map.of(
                        server.getId(), pointsToGrant,
                        "global", pointsToGrant
                );
                apiUser.updateUserPointsGuildID(guildPointsToUpdate);
            } catch (IOException e) {
                Main.getLogger().error("Failed to update points for user {}", messageAuthor.getId(), e);
            }
        }

    }
}
