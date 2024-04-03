package com.sidpatchy.clairebot.Embed.Commands.Regular;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class QuoteEmbed {
    public static CompletableFuture<EmbedBuilder> getQuote(Server server, final User user, TextChannel channel) {

        return channel.getMessages(50000).thenApply(messages -> {
            List<Message> userMessages = messages.stream()
                    .filter(message -> message.getAuthor().getId() == user.getId())
                    .toList();

            if (userMessages.isEmpty()) {
                // user not sent messages
                return ErrorEmbed.getError(Main.getErrorCode("UserNotInSet"));
            }

            Random random = new Random();
            int rand = random.nextInt(userMessages.size());
            Message randomMessage = userMessages.get(rand);

            return new EmbedBuilder()
                    .setColor(Main.getColor(user.getIdAsString()))
                    .setAuthor(user.getDisplayName(server), randomMessage.getLink().toString(), user.getAvatar())
                    .setDescription(randomMessage.getContent())
                    .setTimestamp(randomMessage.getCreationTimestamp())
                    .setFooter(randomMessage.getIdAsString());
        });
    }
}
