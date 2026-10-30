package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

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
    public static CompletableFuture<EmbedBuilder> getQuote(LanguageManager languageManager, Guild server, final User user, MessageChannel channel) {

        return channel.getIterableHistory().takeAsync(50000).thenApply(messages -> {
            List<Message> userMessages = new java.util.ArrayList<>(messages.stream()
                    .filter(message -> message.getAuthor().getIdLong() == user.getIdLong())
                    .toList());

            if (userMessages.isEmpty()) {
                // user not sent messages
                return ErrorEmbed.getError(languageManager, Main.getErrorCode("UserNotInSet"));
            }

            Random random = new Random();

            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Main.getColor(user.getId()));

            boolean messageSelected = false;
            int numberOfAttempts = 0;
            int maxNumberOfAttempts = Math.min(userMessages.size(), 100);

            // Attempt to select a message 100 times.
            // todo validate that this won't nuke the bot
            while (!messageSelected && numberOfAttempts <= maxNumberOfAttempts) {
                int rand = random.nextInt(userMessages.size());
                Message randomMessage = userMessages.get(rand);

                if (!randomMessage.getContentRaw().isEmpty()) {
                    embed.setDescription(randomMessage.getContentRaw());
                    messageSelected = true;
                }
                if (!randomMessage.getAttachments().isEmpty() && randomMessage.getAttachments().get(0).isImage()) {
                    embed.setThumbnail(randomMessage.getAttachments().get(0).getUrl());
                    messageSelected = true;
                }

                // Set embed details if one of the above statements are valid
                if (!randomMessage.getContentRaw().isEmpty() || (!randomMessage.getAttachments().isEmpty() && randomMessage.getAttachments().get(0).isImage())) {
                    embed.setAuthor(server != null && server.getMember(user) != null ? server.getMember(user).getEffectiveName() : user.getName(), randomMessage.getJumpUrl(), user.getEffectiveAvatarUrl())
                            .setTimestamp(randomMessage.getTimeCreated())
                            .setFooter(randomMessage.getId());
                }

                userMessages.remove(rand);
                numberOfAttempts++;
            }

            // Fallback for if all messages checked were invalid
            if (!messageSelected) {
                embed = ErrorEmbed.getCustomError(languageManager, Main.getErrorCode("invalidMessages"),
                        "Looks like the messages I selected were invalid. Please try again later.");
            }

            return embed;
        });
    }

    public static EmbedBuilder viewOriginalMessageBuilder(MessageChannel channel, Message message) {
        MessageEmbed.Footer footer = message.getEmbeds().get(0).getFooter();
        Message quotedMessage = channel.retrieveMessageById(footer.getText()).complete();

        return new EmbedBuilder()
                .addField("Click to jump to the original message:", quotedMessage.getJumpUrl(), false);
    }
}
