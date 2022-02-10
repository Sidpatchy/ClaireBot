package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Clockwork;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attempt to block phishing attacks on Discord.
 * Disabling this is not and will not be supported.
 */
public class AntiPhish implements MessageCreateListener {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        String messageContent = message.getContent();
        List<String> phishingDomains = Clockwork.getPhishingDomains();

        String domainRegex = "(?:[\\w-]+\\.)+[\\w-]+";
        Pattern pattern = Pattern.compile(domainRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(messageContent);

        List<String> domains = new ArrayList<>();
        while (matcher.find()) {
            domains.add(messageContent.substring(matcher.start(), matcher.end()));
        }

        for (String s : domains) {
            if (phishingDomains.contains(s)) {
                message.delete();
                break;
            }
        }
    }
}
