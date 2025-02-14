package com.sidpatchy.clairebot.Lang;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerChannel;

import java.util.*;
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

        return Map.of(
                // Server placeholders
                "cb.server.name", () ->
                        context.getServer() != null ? context.getServer().getName() : "", "cb.server.id", () ->
                        context.getServer() != null ? context.getServer().getIdAsString() : "",

                // User placeholders
                "cb.user.name", () ->
                        context.getUser() != null ? context.getUser().getName() : "", "cb.user.id", () ->
                        context.getUser() != null ? context.getUser().getIdAsString() : "",

                // Author placeholders
                "cb.author.name", () ->
                        context.getAuthor() != null ? context.getAuthor().getName() : "", "cb.author.id", () ->
                        context.getAuthor() != null ? context.getAuthor().getIdAsString() : "",

                // Channel placeholders
                "cb.channel.name", () ->
                        Optional.ofNullable(context.getChannel())
                                .flatMap(Channel::asServerChannel)
                                .map(ServerChannel::getName)
                                .orElse("NOT FOUND"), "cb.channel.id", () ->
                        context.getChannel() != null ? context.getChannel().getIdAsString() : "");
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

    public List<String> process(List<String> input) {
        return input.stream()
                .map(this::process)
                .toList();
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
