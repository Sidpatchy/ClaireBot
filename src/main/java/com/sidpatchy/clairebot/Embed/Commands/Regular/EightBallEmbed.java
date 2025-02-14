package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EightBallEmbed {

    public static EmbedBuilder getEightBall(LanguageManager languageManager, String query, User author) {

        List<String> eightBall = languageManager.getLocalizedList("ClaireLang.Embed.Commands.Regular.EightBallEmbed.8bResponses");
        List<String> eightBallRigged = languageManager.getLocalizedList("ClaireLang.Embed.Commands.Regular.EightBallEmbed.8bRiggedResponses");
        List<String> onTopTriggers = languageManager.getLocalizedList("ClaireLang.Embed.Commands.Regular.EightBallEmbed.OnTopTriggers");
        String ateBallLanguageString = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.EightBallEmbed.8ball");

        Main.getLogger().error(onTopTriggers.toString());

        Random random = new Random();
        int rand = random.nextInt(eightBall.size());

        String response = eightBall.get(rand);
        // Overwrite response if ClaireBot on top trigger
        for (String trigger : onTopTriggers) {
            if (query.toUpperCase().contains(trigger.toUpperCase())) {
                rand = random.nextInt(eightBallRigged.size());
                response = eightBallRigged.get(rand);
            }
        }
        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor(ateBallLanguageString)
                .addField(query, response)
                .setFooter(author.getDiscriminatedName(), author.getAvatar());
    }
}
