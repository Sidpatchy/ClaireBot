package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.API.APIUser;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Leveling.LevelingTools;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.io.IOException;
import java.util.List;
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

        if (messageAuthor.isBotUser() || messageAuthor.isYourself()) {
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

        // Grant between 0 and 8 points
        if (server != null) {
            Integer currentPoints = LevelingTools.getUserPoints(messageAuthor.getIdAsString(), "global");
            RandomGenerator randomGenerator = RandomGenerator.getDefault();
            Integer pointsToGrant = randomGenerator.nextInt(8);
            try {
                apiUser.updateUserPointsGuildID(server.getIdAsString(), pointsToGrant);
                apiUser.updateUserPointsGuildID("global", pointsToGrant);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
