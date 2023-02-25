package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.VotingUtils;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Arrays;
import java.util.List;

public class VotingEmbed {


    /**
     * Called when a user creates a poll or request and specifies a description.
     *
     * @param commandName default will be "REQUEST" or "POLL", no reason to maintain two classes that do basically the same thing (aka pulling a ClaireBot 2)
     * @param question The question the user is asking.
     * @param description A description of what is being asked.
     * @param allowMultipleChoices allow a user to vote for multiple options
     * @param choices List of choices as a string
     * @param server The server/guild that the command is being run in
     * @param author The user who ran the command
     * @param numChoices The number of choices
     * @return voting embed
     */
    public static EmbedBuilder getPoll(String commandName, String question, String description, Boolean allowMultipleChoices, List<String> choices, Server server, User author, Integer numChoices) {
        List<String> emoji = Arrays.asList("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟", "\uD83D\uDC4D", "\uD83D\uDC4E");

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()));

        StringBuilder choiceBuilder = new StringBuilder();
        if (choices != null) {
            for (int i = 0; i < 10; i++) {
                if (choices.get(i) != null) {
                    choiceBuilder.append(emoji.get(i)).append(" ").append(choices.get(i)).append("\n");
                }
                else {break;}
            }
        }

        if (choiceBuilder.toString().equalsIgnoreCase("")) {
            allowMultipleChoices = false;
        }
        else {
            embed.addField("Choices", choiceBuilder.toString());
        }

        embed.setFooter("Poll ID: " + VotingUtils.getPollID(allowMultipleChoices, author.getIdAsString(), numChoices.toString()));

        if (commandName.equalsIgnoreCase("REQUEST")) {
            embed.setAuthor(author.getDisplayName(server) + " requests:", "https://discord.com/users/" + author.getIdAsString(), author.getAvatar());
        }
        else if (commandName.equalsIgnoreCase("POLL")) {
            embed.setAuthor(author.getDisplayName(server) + " asks:", "https://discord.com/users/" + author.getIdAsString(), author.getAvatar());
        }

        if (description.equalsIgnoreCase("")) {
            embed.setDescription(question);
        }
        else {
            embed.addField(question, description);
        }

        return embed;
    }

    /**
     * Called when a user creates a poll without specifying a description.
     *
     * @param commandName default will be "REQUEST" or "POLL", no reason to maintain two classes that do basically the same thing (aka pulling a ClaireBot 2)
     * @param question The question the user is asking.
     * @param allowMultipleChoices allow a user to vote for multiple options
     * @param choices List of choices as a string
     * @param server The server/guild that the command is being run in
     * @param author The user who ran the command
     * @param numChoices The number of choices
     * @return voting embed
     */
    public static EmbedBuilder getPoll(String commandName, String question, Boolean allowMultipleChoices, List<String> choices, Server server, User author, Integer numChoices) {
        return getPoll(commandName, question, "", allowMultipleChoices, choices, server, author, numChoices);
    }

    /**
     * The embed we respond to the user with, should ideally be ephemeral.
     * @param author the author of the command
     * @param requestsChannelMentionTag the channel the request is being posted in
     * @return
     */
    public static EmbedBuilder getUserResponse(User author, String requestsChannelMentionTag) {
        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor("Your request has been created!")
                .setDescription("Go check it out in " + requestsChannelMentionTag);
    }
}
