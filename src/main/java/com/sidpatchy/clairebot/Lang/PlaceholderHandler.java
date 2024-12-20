package com.sidpatchy.clairebot.Lang;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerChannel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderHandler {
    private final ContextManager context;
    private final Map<String, PlaceholderProvider> placeholders;

    // Functional interface for placeholder value providers
    @FunctionalInterface
    private interface PlaceholderProvider {
        String getValue();
    }

    public PlaceholderHandler(ContextManager context) {
        this.context = context;
        this.placeholders = initializePlaceholders();
    }

    private Map<String, PlaceholderProvider> initializePlaceholders() {
        Map<String, PlaceholderProvider> map = new HashMap<>();

        // Server placeholders
        map.put("cb.server.name", () ->
                context.getServer() != null ? context.getServer().getName() : "");
        map.put("cb.server.id", () ->
                context.getServer() != null ? context.getServer().getIdAsString() : "");

        // User placeholders
        map.put("cb.user.name", () ->
                context.getUser() != null ? context.getUser().getName() : "");
        map.put("cb.user.id", () ->
                context.getUser() != null ? context.getUser().getIdAsString() : "");

        // Author placeholders
        map.put("cb.author.name", () ->
                context.getAuthor() != null ? context.getAuthor().getName() : "");
        map.put("cb.author.id", () ->
                context.getAuthor() != null ? context.getAuthor().getIdAsString() : "");

        // Channel placeholders
        map.put("cb.channel.name", () ->
                Optional.ofNullable(context.getChannel())
                        .flatMap(Channel::asServerChannel)
                        .map(ServerChannel::getName)
                        .orElse("NOT FOUND"));
        map.put("cb.channel.id", () ->
                context.getChannel() != null ? context.getChannel().getIdAsString() : "");

        return Collections.unmodifiableMap(map);
    }

    /**
     * Process a string containing placeholders
     * @param input String containing placeholders in format {cb.placeholder.name}
     * @return Processed string with placeholders replaced with their values
     */
    public String process(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(input);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = getPlaceholderValue(placeholder);
            // Quote the replacement string to handle special regex characters
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Get the value for a specific placeholder
     * @param key The placeholder key (without {} brackets)
     * @return The placeholder value or the original key if not found
     */
    public String getPlaceholderValue(String key) {
        PlaceholderProvider provider = placeholders.get(key);
        return provider != null ? provider.getValue() : key;
    }

    /**
     * Check if a placeholder exists
     * @param key The placeholder key (without {} brackets)
     * @return true if the placeholder exists
     */
    public boolean hasPlaceholder(String key) {
        return placeholders.containsKey(key);
    }

    /**
     * Add a custom placeholder
     * @param key The placeholder key (without {} brackets)
     * @param provider The provider function that returns the placeholder value
     */
    public void addPlaceholder(String key, PlaceholderProvider provider) {
        placeholders.put(key, provider);
    }
}
