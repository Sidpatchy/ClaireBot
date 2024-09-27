package com.sidpatchy.clairebot.Util.Cache;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.user.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A message caching system to assist in
 */
public class MessageCacheManager {
    private static Map<String, MessageCacheEntry> messageCache = new ConcurrentHashMap<>();

    public static void purgeCache(long secondsAged) {
        for (MessageCacheEntry entry : messageCache.values()) {
            if (entry.getTimeAdded() < System.currentTimeMillis() - (secondsAged * 1000)) {
                messageCache.remove(entry.getChannel().getIdAsString());
            }
        }
    }

    public static CompletableFuture<List<Message>> queryMessageCache(TextChannel channel, User user) {
        if (messageCache.containsKey(channel.getIdAsString())) {
            // Retrieve messages from the cache and filter them by user
            List<Message> filteredMessages = messageCache.get(channel.getIdAsString())
                    .getMessages().stream()
                    .filter(message -> message.getAuthor().getId() == user.getId())
                    .toList();
            return CompletableFuture.completedFuture(filteredMessages);
        } else {
            // Fetch messages from the channel and filter them based on the user
            return channel.getMessages(50000).thenApply(messages -> {
                MessageCacheEntry newEntry = new MessageCacheEntry(channel, System.currentTimeMillis(), messages.stream().toList());
                // Now you can add this entry to your cache or process it further
                messageCache.put(channel.getIdAsString(), newEntry);
                return messages.stream()
                        .filter(message -> message.getAuthor().getId() == user.getId())
                        .toList();
            }).exceptionally(ex -> {
                // Handle any exceptions that occur during the message retrieval
                ex.printStackTrace();
                return null;
            });
        }
    }
}

