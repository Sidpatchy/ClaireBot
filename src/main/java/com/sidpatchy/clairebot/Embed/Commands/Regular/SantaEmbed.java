package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.SantaUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.*;

public class SantaEmbed {

    private static final String basePath = "ClaireLang.Embed.Commands.Regular.SantaEmbed";

    public static EmbedBuilder getConfirmationEmbed(LanguageManager languageManager, User author) {
        return new EmbedBuilder()
                .setColor(Main.getColor(author.getId()))
                .setAuthor("SecretClaire", "", "https://github.com/Sidpatchy/ClaireBot/blob/main/img/ClaireBot-SantaHat.png?raw=true")
                .setDescription(languageManager.getLocalizedString(basePath, "Confirmation"));
    }

    /**
     * Method for constructing the message to send to the host of the exchange.
     *
     * @param role The group of users participating
     * @param author Author of the command.
     * @param rules Rules for the exchange, separated by \n.
     * @param theme Theme for the exchange.
     * @return Message with components
     */
    public static MessageCreateBuilder getHostMessage(LanguageManager languageManager, Role role, User author, String rules, String theme) {
        Set<User> users =  new HashSet<>();
        for (var member : role.getGuild().getMembersWithRoles(role)) users.add(member.getUser());
        Guild server = role.getGuild();

        MessageCreateBuilder message = new MessageCreateBuilder();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor(author.getId()))
                .setAuthor("SecretClaire", "", "https://github.com/Sidpatchy/ClaireBot/blob/main/img/ClaireBot-SantaHat.png?raw=true");

        String footerText = SantaUtils.getSantaID(server.getId(), author.getId(), role.getId());
        if (server.getIconUrl() != null) {
            embed.setFooter(footerText, server.getIconUrl());
        } else {
            embed.setFooter(footerText);
        }

        if (!theme.isEmpty()) {
            embed.addField("Theme", theme, false);
        }

        if (!rules.isEmpty()) {
            embed.addField("Rules", rules, false);
        }

        HashMap<User, User> santaList = assignSecretSanta(users);

        for (Map.Entry<User, User> userPair : santaList.entrySet()) {
            User giver = userPair.getKey();
            User receiver = userPair.getValue();

            embed.addField(giver.getId(), giver.getAsMention() + " → " + receiver.getAsMention(), false);
        }

        ActionRow actionRow = ActionRow.of(
                Button.primary("rules", languageManager.getLocalizedString(basePath, "RulesButton")),
                Button.primary("theme", languageManager.getLocalizedString(basePath, "ThemeButton")),
                Button.danger("send", languageManager.getLocalizedString(basePath, "SendButton")),
                Button.success("test", languageManager.getLocalizedString(basePath, "TestButton")),
                Button.secondary("randomize", languageManager.getLocalizedString(basePath, "RandomizeButton")));

        message.addEmbeds(embed.build());
        message.addComponents(actionRow);
        return message;
    }

    public static MessageCreateBuilder getSantaMessage(LanguageManager languageManager, Guild server, User author, User giver, User receiver, String rules, String theme) {

        MessageCreateBuilder message = new MessageCreateBuilder();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor(author.getId()))
                .setAuthor("SecretClaire", "", "https://github.com/Sidpatchy/ClaireBot/blob/main/img/ClaireBot-SantaHat.png?raw=true")
                .setFooter(languageManager.getLocalizedString(basePath, "SentByAuthor") + " " + author.getName(), author.getEffectiveAvatarUrl());

        if (!theme.isEmpty()) {
            embed.addField("Theme", theme, false);
        }

        if (!rules.isEmpty()) {
            embed.addField("Rules", rules, false);
        }

        languageManager.addContext(ContextManager.ContextType.SANTA, "giver", server.getMember(giver) != null ? server.getMember(giver).getEffectiveName() : giver.getName());
        languageManager.addContext(ContextManager.ContextType.SANTA, "receiver", server.getMember(receiver) != null ? server.getMember(receiver).getEffectiveName() : receiver.getName());

        embed.setDescription("Ho! Ho! Ho! You have received **" + (server.getMember(receiver) != null ? server.getMember(receiver).getEffectiveName() : receiver.getName()) + "** in the " + server.getName() + " Secret Santa!");

        message.addEmbeds(embed.build());

        return message;
    }

    /**
     * Method implementing a simple selected-cycle approach for pairing users.
     *
     * @param participants the set of users participating in the exchange
     * @return A shuffled hashmap of paired up users
     */
    private static HashMap<User, User> assignSecretSanta(Set<User> participants) {
        List<User> userList = new ArrayList<>(participants);

        // Shuffle the list to ensure random assignment
        Collections.shuffle(userList);

        HashMap<User, User> users = new HashMap<>(); //giver, receiver

        // Creating a directed cycle
        for (int i = 0; i < userList.size(); i++) {
            User giver = userList.get(i);
            User receiver = userList.get((i + 1) % userList.size());

            // Assign the receiver to the giver here
            users.put(giver, receiver);
        }

        return users;
    }

    public static EmbedBuilder buildHostEmbedFromPairs(LanguageManager languageManager, Role role, User author, String rules, String theme, java.util.List<User> givers, java.util.List<User> receivers) {
        Guild server = role.getGuild();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor(author.getId()))
                .setAuthor("SecretClaire", "", "https://github.com/Sidpatchy/ClaireBot/blob/main/img/ClaireBot-SantaHat.png?raw=true");

        String footerText = SantaUtils.getSantaID(server.getId(), author.getId(), role.getId());
        if (server.getIconUrl() != null) {
            embed.setFooter(footerText, server.getIconUrl());
        } else {
            embed.setFooter(footerText);
        }

        if (!theme.isEmpty()) {
            embed.addField("Theme", theme, false);
        }

        if (!rules.isEmpty()) {
            embed.addField("Rules", rules, false);
        }

        for (int i = 0; i < Math.min(givers.size(), receivers.size()); i++) {
            User giver = givers.get(i);
            User receiver = receivers.get(i);
            embed.addField(giver.getId(), giver.getAsMention() + " → " + receiver.getAsMention(), false);
        }

        return embed;
    }

    // Builds a fresh randomized host embed (re-rolls pairs) and preserves footer/format
    public static EmbedBuilder buildHostEmbedRandomized(LanguageManager languageManager, Role role, User author, String rules, String theme) {
        Guild server = role.getGuild();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor(author.getId()))
                .setAuthor("SecretClaire", "", "https://github.com/Sidpatchy/ClaireBot/blob/main/img/ClaireBot-SantaHat.png?raw=true");

        String footerText = SantaUtils.getSantaID(server.getId(), author.getId(), role.getId());
        if (server.getIconUrl() != null) {
            embed.setFooter(footerText, server.getIconUrl());
        } else {
            embed.setFooter(footerText);
        }

        if (!theme.isEmpty()) {
            embed.addField("Theme", theme, false);
        }

        if (!rules.isEmpty()) {
            embed.addField("Rules", rules, false);
        }

        // Re-randomize using the role's current users
        Set<User> users = new HashSet<>();
        for (var member : role.getGuild().getMembersWithRoles(role)) users.add(member.getUser());
        HashMap<User, User> santaList = assignSecretSanta(users);
        for (Map.Entry<User, User> userPair : santaList.entrySet()) {
            User giver = userPair.getKey();
            User receiver = userPair.getValue();
            embed.addField(giver.getId(), giver.getAsMention() + " → " + receiver.getAsMention(), false);
        }

        return embed;
    }
}
