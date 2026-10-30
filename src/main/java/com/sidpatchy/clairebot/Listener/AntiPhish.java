package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Clockwork;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attempt to block phishing attacks on Discord.
 * Disabling this is not and will not be supported.
 */
public class AntiPhish extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String messageContent = message.getContentRaw();
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
                message.delete().queue();
                break;
            }
        }
    }
}
