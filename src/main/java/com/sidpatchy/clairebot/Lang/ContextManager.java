package com.sidpatchy.clairebot.Lang;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.HashMap;
import java.util.Map;

public record ContextManager(
        Server server,
        TextChannel channel,
        User author,
        User user,
        Message message,
        Map<String, Object> dynamicData
) {
    // Enum to define known context types
    public enum ContextType {
        POLL,
        SANTA,
        GENERIC
    }

    // Compact constructor with default empty map
    public ContextManager {
        if (dynamicData == null) {
            dynamicData = new HashMap<>();
        }
    }

    // Alternative constructor without dynamicData parameter
    public ContextManager(Server server, TextChannel channel, User author,
                          User user, Message message) {
        this(server, channel, author, user, message, new HashMap<>());
    }

    // Add dynamic data with type safety
    public void addData(ContextType type, String key, Object value) {
        dynamicData.put(type.name().toLowerCase() + "." + key, value);
    }

    // Get dynamic data with type safety
    @SuppressWarnings("unchecked")
    public <T> T getData(ContextType type, String key) {
        return (T) dynamicData.get(type.name().toLowerCase() + "." + key);
    }

    // Helper functions for specific context types
    public void addPollData(String pollId, String question) {
        addData(ContextType.POLL, "id", pollId);
        addData(ContextType.POLL, "question", question);
    }

    public void addSantaData(User giftee, String theme, String rules) {
        addData(ContextType.SANTA, "giftee", giftee);
        addData(ContextType.SANTA, "theme", theme);
        addData(ContextType.SANTA, "rules", rules);
    }
}
