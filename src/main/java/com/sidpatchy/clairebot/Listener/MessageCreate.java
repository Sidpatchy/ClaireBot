package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.API.APIUser;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class MessageCreate implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        String messageContent = message.getContent();
        Server server = message.getServer().orElse(null);
        MessageAuthor messageAuthor = message.getAuthor();
        User user = messageAuthor.asUser().orElse(null);
        APIUser apiUser = new APIUser(messageAuthor.getIdAsString());
        TextChannel textChannel = message.getChannel();

        ContextManager context = new ContextManager(server, textChannel, user, user, message, new HashMap<>());
        LanguageManager languageManager = new LanguageManager(Main.getFallbackLocale(), context);

        // it seems as though the Javacord functions for this don't actually work, or I'm using them wrong
        if (messageAuthor.isBotUser() || messageAuthor.isYourself() || messageAuthor.getIdAsString().equalsIgnoreCase("704244031772950528") || messageAuthor.getIdAsString().equalsIgnoreCase("848024760789237810")) {
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
                
                // because apparently message.reply() doesn't allow disabling mentions.
                new MessageBuilder()
                        .setContent(onTopResponses.get(rand))
                        .setAllowedMentions(new AllowedMentionsBuilder().build())
                        .replyTo(message)
                        .send(message.getChannel());

                break;
            }
        }

        // pls ban
        List<String> plsBanTriggers = languageManager.getLocalizedList("ClaireLang.PlsBan.PlsBanTriggers");
        List<String> plsBanResponses = languageManager.getLocalizedList("ClaireLang.PlsBan.PlsBanResponses");
        String escapedBotId = Pattern.quote("<@" + Main.getApi().getClientId() + ">");

        for (String trigger : plsBanTriggers) {
            String regex = "(?i)" + escapedBotId + "\\s*" + Pattern.quote(trigger) + ".*";
            if (messageContent.toUpperCase().matches(regex)) {
                Random random = new Random();
                int rand = random.nextInt(plsBanResponses.size());

                // Message.reply() doesn't allow disabling mentions.
                new MessageBuilder()
                        .setContent(plsBanResponses.get(rand))
                        .setAllowedMentions(new AllowedMentionsBuilder().build())
                        .replyTo(message)
                        .send(message.getChannel());

                break;
            }
        }

        // Zerfas react
        for (String trigger : Main.getZerfas()) {
            String regex = ".*\\b" + Pattern.quote(trigger.toUpperCase()) + "\\b.*"; // match trigger led/followed by anything
            if (messageContent.toUpperCase().matches(regex)) {
                Server zerfasEmojiServer = event.getApi().getServerById(Main.getZerfasEmojiServerID()).orElse(null);
                Emoji zerfasEmoji = zerfasEmojiServer.getCustomEmojiById(Main.getZerfasEmojiID()).orElse(null);
                message.addReactions(zerfasEmoji);
            }
        }

        // Grant between 0 and 8 points
        if (server != null) {
            int pointsToGrant = ThreadLocalRandom.current().nextInt(9);
            try {
                Map<String, Integer> guildPointsToUpdate = Map.of(
                        server.getIdAsString(), pointsToGrant,
                        "global", pointsToGrant
                );
                apiUser.updateUserPointsGuildID(guildPointsToUpdate);
            } catch (IOException e) {
                Main.getLogger().error("Failed to update points for user {}", messageAuthor.getIdAsString(), e);
            }
        }

    }
}
