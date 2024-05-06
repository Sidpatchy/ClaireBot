package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class QuoteEmbed {

    /**
     * Retrieves a random quote from the messages sent by a user in a text channel.
     *
     * @param server  the server where the text channel is located
     * @param user    the user whose messages will be considered for quotes
     * @param channel the text channel where the messages are located
     * @return a CompletableFuture that resolves to an EmbedBuilder containing the quote
     */
    public static CompletableFuture<EmbedBuilder> getQuote(Server server, final User user, TextChannel channel) {

        return channel.getMessages(50000).thenApply(messages -> {
            List<Message> userMessages = new java.util.ArrayList<>(messages.stream()
                    .filter(message -> message.getAuthor().getId() == user.getId())
                    .toList());

            if (userMessages.isEmpty()) {
                // user not sent messages
                return ErrorEmbed.getError(Main.getErrorCode("UserNotInSet"));
            }

            Random random = new Random();

            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Main.getColor(user.getIdAsString()));

            boolean messageSelected = false;
            int numberOfAttempts = 0;
            int maxNumberOfAttempts = Math.min(userMessages.size(), 100);

            // Attempt to select a message 100 times.
            // todo validate that this won't nuke the bot
            while (!messageSelected && numberOfAttempts <= maxNumberOfAttempts) {
                int rand = random.nextInt(userMessages.size());
                Message randomMessage = userMessages.get(rand);

                if (!randomMessage.getContent().isEmpty()) {
                    embed.setDescription(randomMessage.getContent());
                    messageSelected = true;
                }
                if (!randomMessage.getAttachments().isEmpty() && randomMessage.getAttachments().get(0).isImage()) {
                    embed.setThumbnail(randomMessage.getAttachments().get(0).asImage().join());
                    messageSelected = true;
                }

                // Set embed details if one of the above statements are valid
                if (!randomMessage.getContent().isEmpty() || (!randomMessage.getAttachments().isEmpty() && randomMessage.getAttachments().get(0).isImage())) {
                    embed.setAuthor(user.getDisplayName(server), randomMessage.getLink().toString(), user.getAvatar())
                            .setTimestamp(randomMessage.getCreationTimestamp())
                            .setFooter(randomMessage.getIdAsString());
                }

                userMessages.remove(rand);
                numberOfAttempts++;
            }

            // Fallback for if all messages checked were invalid
            if (!messageSelected) {
                embed = ErrorEmbed.getCustomError(Main.getErrorCode("invalidMessages"),
                        "Looks like the messages I selected were invalid. Please try again later.");
            }

            return embed;
        });
    }

    public static EmbedBuilder viewOriginalMessageBuilder(TextChannel channel, Message message) {
        EmbedFooter footer = message.getEmbeds().get(0).getFooter().orElse(null);
        Message quotedMessage = message.getApi().getMessageById(footer.getText().orElse(""), channel).join();

        return new EmbedBuilder()
                .addField("Click to jump to the original message:", quotedMessage.getLink().toString());
    }
}
