package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.API.APIUser;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Leveling.LevelingTools;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageType;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.regex.Pattern;

public class MessageCreate implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        String messageContent = message.getContent();
        Server server = message.getServer().orElse(null);
        MessageAuthor messageAuthor = message.getAuthor();
        APIUser apiUser = new APIUser(messageAuthor.getIdAsString());

        // it seems as though the Javacord functions for this don't actually work, or I'm using them wrong
        if (messageAuthor.isBotUser() || messageAuthor.isYourself() || messageAuthor.getIdAsString().equalsIgnoreCase("704244031772950528") || messageAuthor.getIdAsString().equalsIgnoreCase("848024760789237810")) {
            Main.getLogger().debug("Detected bot user, skipping onMesssage checks!");
            return;
        }

        // ClaireBot on top!!
        List<String> onTopResponses = Main.getClaireBotOnTopResponses();
        for (String trigger : Main.getOnTopTriggers()) {
            String regex = "\\b" + Pattern.quote(trigger.toUpperCase()) + "\\b.*"; // match trigger followed by anything
            if (messageContent.toUpperCase().matches(regex)) {
                Random random = new Random();
                int rand = random.nextInt(onTopResponses.size());
                
                // because apparently message.reply() doesn't allow disabling mentions.
                new MessageBuilder()
                        .setContent(Main.getClaireBotOnTopResponses().get(rand))
                        .setAllowedMentions(new AllowedMentionsBuilder().build())
                        .replyTo(message)
                        .send(message.getChannel());

                break;
            }
        }

        // pls ban
        List<String> plsBanResponses = Main.getPlsBanResponses();
        String escapedBotId = Pattern.quote("<@" + Main.getApi().getClientId() + ">");

        for (String trigger : Main.getPlsBanTriggers()) {
            String regex = "(?i)" + escapedBotId + "\\s*" + Pattern.quote(trigger) + ".*";
            if (messageContent.toUpperCase().matches(regex)) {
                Random random = new Random();
                int rand = random.nextInt(plsBanResponses.size());

                // Message.reply() doesn't allow disabling mentions.
                new MessageBuilder()
                        .setContent(Main.getPlsBanResponses().get(rand))
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
            Integer currentPoints = LevelingTools.getUserPoints(messageAuthor.getIdAsString(), "global");
            RandomGenerator randomGenerator = RandomGenerator.getDefault();
            Integer pointsToGrant = randomGenerator.nextInt(8);
            try {
                Map<String, Integer> guildPointsToUpdate = new HashMap<>();
                guildPointsToUpdate.put(server.getIdAsString(), pointsToGrant);
                guildPointsToUpdate.put("global", pointsToGrant);
                apiUser.updateUserPointsGuildID(guildPointsToUpdate);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
