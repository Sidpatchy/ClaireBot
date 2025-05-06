package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.SantaUtils;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.*;

public class SantaEmbed {

    private static final String basePath = "ClaireLang.Embed.Commands.Regular.SantaEmbed";

    public static EmbedBuilder getConfirmationEmbed(LanguageManager languageManager, User author) {
        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
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
    public static MessageBuilder getHostMessage(LanguageManager languageManager, Role role, User author, String rules, String theme) {
        Set<User> users =  role.getUsers();
        Server server = role.getServer();

        MessageBuilder message = new MessageBuilder();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor("SecretClaire", "", "https://github.com/Sidpatchy/ClaireBot/blob/main/img/ClaireBot-SantaHat.png?raw=true")
                .setFooter(SantaUtils.getSantaID(server.getIdAsString(), author.getIdAsString(), role.getIdAsString()), server.getIcon().orElse(null));

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

            embed.addField(giver.getIdAsString(), giver.getNicknameMentionTag() + " → " + receiver.getNicknameMentionTag(), false);
        }

        ActionRow actionRow = ActionRow.of(
                Button.primary("rules", languageManager.getLocalizedString(basePath, "RulesButton")),
                Button.primary("theme", languageManager.getLocalizedString(basePath, "ThemeButton")),
                Button.danger("send", languageManager.getLocalizedString(basePath, "SendButton")),
                Button.success("test", languageManager.getLocalizedString(basePath, "TestButton")),
                Button.secondary("randomize", languageManager.getLocalizedString(basePath, "RandomizeButton")));

        message.addEmbed(embed);
        message.addComponents(actionRow);
        return message;
    }

    public static MessageBuilder getSantaMessage(LanguageManager languageManager, Server server, User author, User giver, User receiver, String rules, String theme) {

        MessageBuilder message = new MessageBuilder();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor("SecretClaire", "", "https://github.com/Sidpatchy/ClaireBot/blob/main/img/ClaireBot-SantaHat.png?raw=true")
                .setFooter(languageManager.getLocalizedString(basePath, "SentByAuthor") + " " + author.getName(), author.getAvatar());

        if (!theme.isEmpty()) {
            embed.addField("Theme", theme, false);
        }

        if (!rules.isEmpty()) {
            embed.addField("Rules", rules, false);
        }

        languageManager.addContext(ContextManager.ContextType.SANTA, "giver", giver.getDisplayName(server));
        languageManager.addContext(ContextManager.ContextType.SANTA, "receiver", receiver.getDisplayName(server));

        embed.setDescription("Ho! Ho! Ho! You have received **" + receiver.getDisplayName(server) + "** in the " + server.getName() + " Secret Santa!");

        message.addEmbed(embed);

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
}
