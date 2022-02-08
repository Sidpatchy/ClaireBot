package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Main;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attempt to block phishing attacks on Discord using several open
 */
public class AntiPhish implements MessageCreateListener {

    Yaml yaml = new Yaml();
    Logger logger = Main.getLogger();

    List<String> phishingLinks = readYAMLFromURL("https://raw.githubusercontent.com/nikolaischunk/discord-phishing-links/main/domain-list.json");
    DiscordApi api = Main.getApi();

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        String messageContent = message.getContent();

        if (message.getAuthor().getIdAsString().equalsIgnoreCase("595452406439870464")) {
            return;
        }

        String domainRegex = "(?:[\\w-]+\\.)+[\\w-]+";
        Pattern pattern = Pattern.compile(domainRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(messageContent);

        List<String> domains = new ArrayList<>();
        while (matcher.find()) {
            domains.add(messageContent.substring(matcher.start(), matcher.end()));
        }

        for (String s : domains) {
            if (phishingLinks.contains(s)) {
                message.delete();
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> readYAMLFromURL(String link) {
        URL url = null;
        InputStreamReader reader = null;
        try {
            url = new URL(link);
            reader = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            logger.error(e.getStackTrace());
            logger.error("Unable to read from " + link);
        }

        Map<String, Object> document = yaml.load(reader);

        return (List<String>) document.get("domains");
    }
}
