package com.sidpatchy.clairebot.Util;

import com.sidpatchy.clairebot.Main;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedField;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SantaUtils {
    public static class ExtractionResult {
        public String rules;
        public String theme;
        public List<User> givers;
        public List<User> receivers;
        public HashMap<String, String> santaID;

        public ExtractionResult(String rules, String theme, List<User> givers, List<User> receivers, HashMap<String, String> santaID) {
            this.rules = rules;
            this.theme = theme;
            this.givers = givers;
            this.receivers = receivers;
            this.santaID = santaID;
        }
    }

    public static ExtractionResult extractDataFromEmbed(Embed embed, EmbedFooter footer) {
        String rules = "";
        String theme = "";
        List<User> givers = new ArrayList<>();
        List<User> receivers = new ArrayList<>();
        for (EmbedField field : embed.getFields()) {
            String name = field.getName();
            String value = field.getValue();

            if (name.equalsIgnoreCase("rules")) {
                rules = value;
            } else if (name.equalsIgnoreCase("theme")) {
                theme = value;
            } else {
                value = value.replace("<@!", "");
                value = value.replace(">", "");
                value = value.replace(" → ", "");
                value = value.replace(name, "");

                givers.add(Main.getApi().getUserById(name).join());
                receivers.add(Main.getApi().getUserById(value).join());
            }
        }

        assert footer != null;
        HashMap<String, String> santaID = parseSantaID(footer.getText().orElse(null));

        return new ExtractionResult(rules, theme, givers, receivers, santaID);
    }

    public static String getSantaID(String serverID, String authorID, String roleID) {
        return serverID + ":" + authorID + ":" + roleID;
    }

    public static HashMap<String, String> parseSantaID(String id) {
        List<String> entries = Arrays.asList(StringUtils.splitPreserveAllTokens(id, ":"));

        return new HashMap<>() {{
            put("serverID", entries.get(0));
            put("authorID", entries.get(1));
            put("roleID", entries.get(2));
        }};
    }
}
