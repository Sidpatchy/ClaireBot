package com.sidpatchy.clairebot.Util;

import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedField;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.entity.user.User;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
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
        long serverIdLong = Long.parseLong(serverID);
        long authorIdLong = Long.parseLong(authorID);
        long roleIdLong = Long.parseLong(roleID);

        // Pack into bytes: 8 bytes per ID = 24 bytes total
        ByteBuffer buffer = ByteBuffer.allocate(24);
        buffer.putLong(serverIdLong);
        buffer.putLong(authorIdLong);
        buffer.putLong(roleIdLong);

        // Base64 encode (URL-safe, no padding)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
    }

    public static HashMap<String, String> parseSantaID(String id) {
        byte[] bytes = Base64.getUrlDecoder().decode(id);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        long serverId = buffer.getLong();
        long authorId = buffer.getLong();
        long roleId = buffer.getLong();

        return new HashMap<>() {{
            put("serverID", String.valueOf(serverId));
            put("authorID", String.valueOf(authorId));
            put("roleID", String.valueOf(roleId));
        }};
    }
}
