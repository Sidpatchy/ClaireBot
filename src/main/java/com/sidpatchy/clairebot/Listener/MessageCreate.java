package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.Commands.Regular.AvatarEmbed;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class MessageCreate implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        String messageContent = message.getContent();

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
    }
}
