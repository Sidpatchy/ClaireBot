package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class EightBallEmbed {

    public static EmbedBuilder getEightBall(String query, User author) {

        ArrayList<String> eightBall = (ArrayList<String>) Main.getEightBall();
        ArrayList<String> eightBallRigged = (ArrayList<String>) Main.getEightBallRigged();
        ArrayList<String> onTopTriggers = (ArrayList<String>) Main.getOnTopTriggers();

        Random random = new Random();
        int rand = random.nextInt(eightBall.size());

        String response = eightBall.get(rand);
        // Overwrite response if ClaireBot on top trigger
        for (String s : onTopTriggers) {
            if (query.toUpperCase().contains(s.toUpperCase())) {
                rand = random.nextInt(eightBallRigged.size());
                response = eightBallRigged.get(rand);
            }
        }
        return new EmbedBuilder()
                .setColor(Main.getColor())
                .setAuthor("8ball")
                .addField(query, response)
                .setFooter(author.getDiscriminatedName(), author.getAvatar());
    }
}
