package com.sidpatchy.clairebot.Util;

import com.sidpatchy.clairebot.Main;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;

public class MessageUtils {
    static DiscordApi api = Main.getApi();

    public static boolean isBotUser(Message message) {
        return message.getAuthor().getId() == api.getClientId();
    }
}
