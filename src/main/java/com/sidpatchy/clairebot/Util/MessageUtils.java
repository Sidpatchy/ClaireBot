package com.sidpatchy.clairebot.Util;

import com.sidpatchy.clairebot.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;

public class MessageUtils {
    static JDA api = Main.getApi();

    public static boolean isBotUser(Message message) {
        return message.getAuthor().getIdLong() == api.getSelfUser().getIdLong();
    }
}
