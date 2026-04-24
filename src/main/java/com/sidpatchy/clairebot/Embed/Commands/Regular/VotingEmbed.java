package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Voting.VotingUtils;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Arrays;
import java.util.List;

public class VotingEmbed {

    /**
     * Called when a user creates a poll or request and specifies a description.
     *
     * @param commandName default will be "REQUEST" or "POLL"
     * @param question The question the user is asking.
     * @param description A description of what is being asked.
     * @param allowMultipleChoices allow a user to vote for multiple options
     * @param choices List of choices as a string
     * @param server The server/guild that the command is being run in
     * @param author The user who ran the command
     * @param numChoices The number of choices
     * @return voting embed
     */
    public static EmbedBuilder getPoll(LanguageManager languageManager,
                                       String commandName,
                                       String question,
                                       String description,
                                       Boolean allowMultipleChoices,
                                       List<String> choices,
                                       Server server,
                                       User author,
                                       Integer numChoices) {
        // Temp/localized variables block
        List<String> emoji = Arrays.asList("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟", "\uD83D\uDC4D", "\uD83D\uDC4E");
        String choicesLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.VotingEmbed.Choices");
        String pollRequestText;
        String pollAskText;
        {
            // Provide display name placeholder for author line
            languageManager.addContext(ContextManager.ContextType.GENERIC, "user.id.displayname.server", author.getDisplayName(server));
            pollRequestText = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.VotingEmbed.PollRequest");
            pollAskText = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.VotingEmbed.PollAsk");
        }
        String authorUrl = "https://discord.com/users/" + author.getIdAsString();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()));

        // Build choices block (up to 10)
        StringBuilder choiceBuilder = new StringBuilder();
        if (choices != null) {
            int limit = Math.min(10, choices.size());
            for (int i = 0; i < limit; i++) {
                String choice = choices.get(i);
                if (choice == null || choice.isBlank()) {
                    break;
                }
                choiceBuilder.append(emoji.get(i)).append(" ").append(choice).append("\n");
            }
        }

        if (choiceBuilder.isEmpty()) {
            allowMultipleChoices = false;
        } else {
            embed.addField(choicesLabel, choiceBuilder.toString());
        }

        // Compute poll ID after choices logic, then localize footer with placeholder
        String pollId = VotingUtils.getPollID(allowMultipleChoices, author.getIdAsString(), numChoices.toString());
        languageManager.addContext(ContextManager.ContextType.GENERIC, "poll.id", pollId);
        String footer = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.VotingEmbed.PollID");
        embed.setFooter(footer);

        // Localized author line
        if (commandName.equalsIgnoreCase("REQUEST")) {
            embed.setAuthor(pollRequestText, authorUrl, author.getAvatar());
        } else if (commandName.equalsIgnoreCase("POLL")) {
            embed.setAuthor(pollAskText, authorUrl, author.getAvatar());
        }

        // Question / description
        if (description == null || description.isEmpty()) {
            embed.setDescription(question);
        } else {
            embed.addField(question, description);
        }

        return embed;
    }

    /**
     * Called when a user creates a poll without specifying a description.
     */
    public static EmbedBuilder getPoll(LanguageManager languageManager,
                                       String commandName,
                                       String question,
                                       Boolean allowMultipleChoices,
                                       List<String> choices,
                                       Server server,
                                       User author,
                                       Integer numChoices) {
        return getPoll(languageManager, commandName, question, "", allowMultipleChoices, choices, server, author, numChoices);
    }

    /**
     * The embed we respond to the user with, should ideally be ephemeral.
     * @param author the author of the command
     * @param requestsChannelMentionTag the channel the request is being posted in (e.g. "<#1234567890>")
     */
    public static EmbedBuilder getUserResponse(LanguageManager languageManager, User author, String requestsChannelMentionTag) {
        // Temp/localized variables block
        String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.VotingEmbed.UserResponseTitle");
        languageManager.addContext(ContextManager.ContextType.GENERIC, "cb.channel.requests.mentiontag", requestsChannelMentionTag);
        String desc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.VotingEmbed.UserResponseDescription");

        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor(title)
                .setDescription(desc);
    }
}
