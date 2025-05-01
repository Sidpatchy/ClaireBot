package com.sidpatchy.clairebot.Lang;

import com.sidpatchy.clairebot.Main;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerChannel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;

public class PlaceholderHandler {
    private final ContextManager context;
    private final Map<String, PlaceholderProvider> placeholders;

    // Functional interface for placeholder value providers
    @FunctionalInterface
    private interface PlaceholderProvider {
        Object getRawValue();  // Returns any type

        default String getValue() {
            return String.valueOf(getRawValue());
        }
    }


    public PlaceholderHandler(ContextManager context) {
        this.context = context;
        this.placeholders = initializePlaceholders();
    }

    private Map<String, PlaceholderProvider> initializePlaceholders() {
        return Map.ofEntries(
                // Project placeholders
                entry("cb.invitelink", Main::getInviteLink),
                entry("cb.docs", Main::getDocumentationWebsite),
                entry("cb.website", Main::getWebsite),
                entry("cb.github", Main::getGithub),
                entry("cb.supportserver", Main::getSupportServer),

                // Bot placeholders
                entry("cb.bot.numservers", () -> Main.getApi().getServers().size()),
                entry("cb.bot.version", Main::getBuildVersion),
                entry("cb.bot.releasedate", Main::getBuildDate),
                entry("cb.bot.startseconds", () -> Main.getStartMillis() / 1000),
                entry("cb.bot.runtimedurationwords", () -> DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - Main.getStartMillis(), true, false)),

                // Server placeholders
                entry("cb.server.name", () ->
                        context.getServer() != null ? context.getServer().getName() : ""),
                entry("cb.server.id", () ->
                        context.getServer() != null ? context.getServer().getIdAsString() : ""),

                // User placeholders
                entry("cb.user.name", () ->
                        context.getUser() != null ? context.getUser().getName() : ""),
                entry("cb.user.id", () ->
                        context.getUser() != null ? context.getUser().getIdAsString() : ""),

                // Author placeholders
                entry("cb.author.name", () ->
                        context.getAuthor() != null ? context.getAuthor().getName() : ""),
                entry("cb.author.id", () ->
                        context.getAuthor() != null ? context.getAuthor().getIdAsString() : ""),

                // Channel placeholders
                entry("cb.channel.name", () ->
                        Optional.ofNullable(context.getChannel())
                                .flatMap(Channel::asServerChannel)
                                .map(ServerChannel::getName)
                                .orElse("NOT FOUND")),
                entry("cb.channel.id", () ->
                        context.getChannel() != null ? context.getChannel().getIdAsString() : "")
        );
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
